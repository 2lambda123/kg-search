
/*
*   Copyright (c) 2018, EPFL/Human Brain Project PCO
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/

package editor.helpers

import common.helpers.JsFlattener
import common.models.{NexusInstance, NexusPath, User}
import editor.models.{EditorInstance, ReleaseStatus}
import org.joda.time.DateTime
import org.json4s.JsonAST.{JField, JNull, JObject, JValue}
import org.json4s.native.{JsonMethods, JsonParser}
import org.json4s.{Diff, JsonAST}
import play.api.Logger
import play.api.libs.json.Reads.of
import play.api.libs.json._

import scala.collection.immutable.SortedSet

object InstanceHelper {
    val logger = Logger(this.getClass)

  type UpdateInfo = (String, Int, String)

  def consolidateFromManualSpace(
                                  nexusEndpoint: String,
                                  manualSpace:String,
                                  originalInstance: NexusInstance,
                                  editorInstances: List[EditorInstance],
                                  updateToBeStoredInManual: EditorInstance,
                                  user: User
                                ): (NexusInstance, Option[List[UpdateInfo]]) = {
    logger.debug(s"Result from incoming links $editorInstances")
    val updatesByPriority = buildManualUpdatesFieldsFrequency(
      editorInstances.filter( item => item.updaterId != user.id),
      updateToBeStoredInManual
    )
    val result = reconcilationLogic(updatesByPriority, originalInstance.content).as[NexusInstance]
    val manualUpdateDetailsOpt = if(editorInstances.isEmpty){
      logger.debug("creating new editor instace")
      None
    }else{
      //Call reconcile API
      logger.debug(s"Reconciled instance $result")
      val manualUpdateDetails = editorInstances.map(manualEntity => manualEntity.extractUpdateInfo())
      Some(manualUpdateDetails)
    }
    (result, manualUpdateDetailsOpt)
  }


  def buildDiffEntity(consolidatedResponse: NexusInstance, newValue: NexusInstance, originalInstance: NexusInstance): JsObject = {
    val consolidatedJson = JsonParser.parse(consolidatedResponse.removeNexusFields().content.toString())
    val newJson = JsonParser.parse(newValue.content.toString())
    val Diff(changed, added, deleted) = consolidatedJson.diff(newJson)
    val diff: JsonAST.JValue = (deleted, changed) match {
      case (JsonAST.JNothing, JsonAST.JNothing) =>
        consolidatedJson
      case (JsonAST.JNothing, _) =>
        changed.merge(added)
      case (_, JsonAST.JNothing) =>
        val originalContent = JsonParser.parse(originalInstance.content.toString())
        val Diff(changedFromOrg, _, deletedFromOrg) = originalContent.diff(newJson)
        logger.debug(s"""PARTIAL FORM DEFINITION - missing fields from form: ${deletedFromOrg.toString}""")
        // Here we try to get the diff as being the array without the deleted element
        val Diff(_, add, _) = deletedFromOrg.diff(originalContent)
        val Diff(ch, _ ,_ ) = newJson.diff(add)
        if(ch == JsonAST.JNothing){
          // This means we should display an empty field
          val map: Map[String, JValue] = deletedFromOrg.values.asInstanceOf[Map[String, Any]].map{
            case (k, _) => k -> JNull
          }
          JObject(map.toList.map(x => JField(x._1, x._2)))
        }else{
          changedFromOrg.merge(ch)
        }
      case _ =>
        /* fields deletion. Deletion are allowed on manual space ONLY
         * final diff is then an addition/update from original view
         * this allows partially defined form (some field are then not updatable)
         */
        val originalContent = JsonParser.parse(originalInstance.content.toString())
        val Diff(changedFromOrg, addedFromOrg, deletedFromOrg) = originalContent.diff(newJson)
        changedFromOrg.merge(addedFromOrg)
    }
    val rendered = Json.parse(JsonMethods.compact(JsonMethods.render(diff))).as[JsObject]
    val diffWithCompleteArray = rendered.value.map{
      case (k, v) =>
        val value = ( newValue.content \ k).asOpt[JsValue]
        if(v.asOpt[JsArray].isDefined && value.isDefined){
          k -> value.get
        }else{
          k -> v
        }
    }
    Json.toJson(diffWithCompleteArray).as[JsObject]
  }

  def buildInstanceFromForm(original: NexusInstance, formContent: JsObject, nexusEndpoint: String): NexusInstance = {
//    val flattened = JsFlattener(formContent)
//    applyChanges(original, flattened)
    val cleanForm = FormHelper.removeKey(formContent.as[JsValue])
    val formWithID = cleanForm.toString().replaceAll(""""id":"""", s""""@id":"${nexusEndpoint}/v0/data/""")
    val res= original.content.deepMerge(Json.parse(formWithID).as[JsObject])
    original.copy(content = res)
  }

  def buildNewInstanceFromForm(nexusEndpoint: String, instancePath: NexusPath, formRegistry: JsObject, newInstance: JsObject): JsObject = {

    def addNexusEndpointToLinks(item: JsValue): JsObject = {
      val id = (item.as[JsObject] \ "id" ).as[String]
      if(!id.startsWith("http://")){
        Json.obj("@id" ->  JsString(s"$nexusEndpoint/v0/data/$id"))
      }else{
        Json.obj("@id" ->  JsString(id))
      }
    }

    val fields = (formRegistry \ instancePath.org \ instancePath.domain \ instancePath.schema \ instancePath.version \ "fields").as[JsObject].value
    val m = newInstance.value.map{ case (k, v) =>
      val key = FormHelper.unescapeSlash(k)
      val formObjectType = (fields(key) \ "type").as[String]
      formObjectType match {
        case "DropdownSelect" =>
          val arr: IndexedSeq[JsValue] = v.as[JsArray].value.map{ item =>
            addNexusEndpointToLinks(item)
          }
          key -> Json.toJson(arr)
        case _ =>
          if( (fields(key) \ "isLink").asOpt[Boolean].getOrElse(false)){
            key -> addNexusEndpointToLinks(v)
          } else{
            key -> v
          }
      }
    }
    Json.toJson(m).as[JsObject]
  }

  def md5HashString(s: String): String = {
    import java.math.BigInteger
    import java.security.MessageDigest
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(s.getBytes)
    val bigInt = new BigInteger(1,digest)
    val hashedString = bigInt.toString(16)
    hashedString
  }

  def buildManualUpdatesFieldsFrequency(manualUpdates: List[EditorInstance], currentUpdate: EditorInstance): Map[String, SortedSet[(JsValue, Int)]] = {
    val cleanMap: List[Map[String, JsValue]] = currentUpdate.contentToMap() +: manualUpdates.map(s => s.cleanManualData().contentToMap())
    buildMapOfSortedManualUpdates(cleanMap)
  }

  // build reconciled view from updates statistics
  def reconcilationLogic(frequencies: Map[String, SortedSet[(JsValue, Int)]], origin: JsObject): JsObject = {
    // simple logic: keep the most frequent
    val transformations = frequencies.filterKeys(key => !key.startsWith("http://hbp.eu/manual#")).map {
      case (pathString, freqs) => (JsFlattener.buildJsPathFromString(pathString), freqs.last._1)
    }.toSeq
    applyChanges(origin, transformations)
  }

  // apply a set of transformation to an instance
  def applyChanges(instance: JsObject, changes: Seq[(JsPath, JsValue)]): JsObject = {
    val obj = changes.foldLeft(instance) {
      case (res, (path, value)) =>
        val updatedJson = updateJson(res, path, value)
        updatedJson
    }
    obj
  }

  // return an updated JsObject or the src object in case of failure
  def updateJson(instance: JsObject, path: JsPath, newValue: JsValue): JsObject = {
    val simpleUpdateTransformer = path.json.update(
      of[JsValue].map { js =>
          newValue
        }
    )
    instance.transform(simpleUpdateTransformer) match {
      case s: JsSuccess[JsObject] => s.get
      case e: JsError => logger.error(s"Could not apply json update - ${JsError.toJson(e).toString()}")
        throw new Exception("Could not apply update")
    }
  }

  def buildMapOfSortedManualUpdates(manualUpdates: List[Map[String, JsValue]]): Map[String, SortedSet[(JsValue, Int)]] = {
    implicit val order = Ordering.fromLessThan[(JsValue, Int)](_._2 < _._2)
    // For each update
    val tempMap = manualUpdates.foldLeft(Map.empty[String, List[JsValue]]) {
      case (merged, m) =>
        val tempRes: Map[String, List[JsValue]] = m.foldLeft(merged) { case (acc, (k, v)) =>
          acc.get(k) match {
            case Some(existing) => acc.updated(k, v :: existing)
            case None => acc.updated(k, List(v))
          }
        }
        tempRes
    }
    val sortedSet = tempMap
      .filter(e => e._1 != "@type" &&
        e._1 != "http://hbp.eu/manual#parent" &&
        e._1 != "http://hbp.eu/manual#origin" &&
        e._1 != "http://hbp.eu/manual#updater_id")
      .map { el =>
        val e = el._2.groupBy(identity).mapValues(_.size)
        el._1 -> SortedSet(e.toList: _*)
      }
    sortedSet
  }


  def merge[K, V](maps: Seq[Map[K, V]])(f: (K, V, V) => V): Map[K, V] = {
    maps.foldLeft(Map.empty[K, V]) { case (merged, m) =>
      m.foldLeft(merged) { case (acc, (k, v)) =>
        acc.get(k) match {
          case Some(existing) => acc.updated(k, f(k, existing, v))
          case None => acc.updated(k, v)
        }
      }
    }
  }









  def formatInstanceList(jsArray: JsArray, reconciledSuffix:String): JsValue = {

    val arr = jsArray.value.map { el =>
      val url = (el \ "@id").as[String]
      val name: JsString = (el \ "http://schema.org/name")
        .asOpt[JsString].getOrElse(
        (el \"http://hbp.eu/minds#alias" ).asOpt[JsString].getOrElse( (el \ "http://hbp.eu/minds#title").asOpt[JsString].getOrElse(JsString("")))
      )
      val description: JsString = if ((el \ "http://schema.org/description").isDefined) {
        (el \ "http://schema.org/description").as[JsString]
      } else {
        JsString("")
      }

      val id = url.split("/").last
      val formattedId = NexusPath(url)
        .originalPath(reconciledSuffix)
        .toString() + "/" + id
      Json.obj("id" -> formattedId, "description" -> description, "label" -> name)
    }
    Json.toJson(arr)
  }


  def toReconcileFormat(jsValue: JsValue, privateSpace: String): JsObject = {
    Json.obj("src" -> privateSpace, "content" -> jsValue.as[JsObject].-("@context"))
  }

  //TODO make it dynamic :D
  def getPriority(id: String): Int = {
    if (id contains "manual/poc") {
      3
    } else {
      1
    }
  }

  def getCurrentInstanceDisplayed(currentReconciledInstances: Seq[NexusInstance], originalInstance: NexusInstance): NexusInstance = {
    if (currentReconciledInstances.nonEmpty) {
      val sorted = currentReconciledInstances.sortWith { (left, right) =>
        (left.content \ "http://hbp.eu/reconciled#update_timestamp").as[Long] >
          (right.content \ "http://hbp.eu/reconciled#update_timestamp").as[Long]
      }
      sorted.head
    } else{
      originalInstance
    }
  }







}
