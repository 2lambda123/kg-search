
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

package helpers

import constants.{EditorConstants, SchemaFieldsConstants}
import models._
import models.instance.{EditorInstance, NexusInstance, PreviewInstance}
import org.json4s.JsonAST._
import org.json4s.native.{JsonMethods, JsonParser}
import org.json4s.{Diff, JsonAST}
import play.api.Logger
import play.api.libs.json.Reads.of
import play.api.libs.json._
import services.FormService

import scala.collection.immutable.SortedSet

object InstanceHelper {
    val logger = Logger(this.getClass)

  type UpdateInfo = (Option[String], Int, String, EditorInstance)


  private def handleDeletion(deleted: JValue, newJson: JValue): JValue = {
    implicit class JValueExtended(value: JValue) {
      def has(childString: String): Boolean = {
        if ((value \ childString) != JNothing) {
          true
        } else {
          false
        }
      }
    }
    /* fields deletion. Deletion are allowed on manual space ONLY
     * final diff is then an addition/update from original view
     * this allows partially defined form (some field are then not updatable)
     */
    logger.debug(s"""PARTIAL FORM DEFINITION - missing fields from form: ${deleted.toString}""")
    // Here we get the diff as being the array without the deleted element
    val deletion = deleted.values.asInstanceOf[Map[String, Any]].map {
      case (k, _) =>
        if (!newJson.has(k)) {
          k -> JNull
        } else {
          k -> newJson \ k
        }
    }
    JObject(deletion.toList.map(x => JField(x._1, x._2)))

  }

  def buildDiffEntity(currentInstance: NexusInstance, newValue: NexusInstance): EditorInstance = {
    val consolidatedJson = JsonParser.parse(currentInstance.content.toString())
    val newJson = JsonParser.parse(newValue.content.toString())
    val Diff(changed, added, deleted) = consolidatedJson.diff(newJson)
    val diff: JsonAST.JValue = (deleted, changed) match {
      case (JsonAST.JNothing, JsonAST.JNothing) =>
        added
      case (JsonAST.JNothing, _) =>
        changed.merge(added)
      case (_, JsonAST.JNothing) =>
        val deletion = handleDeletion(deleted, newJson)
        deletion.merge(added)
      case _ =>
        val deletion = handleDeletion(deleted, newJson)
        changed.merge(deletion.merge(added))
    }
    val rendered = Json.parse(JsonMethods.compact(JsonMethods.render(diff))).as[JsObject]
    val diffWithCompleteArray = rendered.value
      .map {
        case (k, v) =>
          val value = (newValue.content \ k).asOpt[JsValue]
          if (v.asOpt[JsArray].isDefined && value.isDefined) {
            k -> value.get
          } else {
            k -> v
          }
      }
    EditorInstance(
      NexusInstance(currentInstance.nexusUUID, currentInstance.nexusPath, Json.toJson(diffWithCompleteArray).as[JsObject])
    )
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


  def formatInstanceList(jsArray: JsArray, reconciledSuffix:String): List[PreviewInstance] = {

    jsArray.value.map { el =>
      val url = (el \ "@id").as[String]
      val name: String = (el \ SchemaFieldsConstants.NAME)
        .asOpt[String].getOrElse(
        (el \"http://hbp.eu/minds#alias" ).asOpt[String].getOrElse( (el \ "http://hbp.eu/minds#title").asOpt[String].getOrElse(""))
      )
      val description: String = if ((el \ SchemaFieldsConstants.DESCRIPTION).isDefined) {
        (el \ SchemaFieldsConstants.DESCRIPTION).as[String]
      } else {
        ""
      }
      val id = url.split("/v0/data/").last
      PreviewInstance(id,name, Some(description))
    }.toList
  }

  def addDefaultFields(instance: NexusInstance,formRegistry:FormRegistry): NexusInstance = {
    val fields = (formRegistry.registry \ instance.nexusPath.org \ instance.nexusPath.domain \ instance.nexusPath.schema \instance.nexusPath.version \ "fields")
      .as[JsObject].value
    val m = fields.map { case (k, v) =>
      val fieldValue =  instance.getField(k)
      if(fieldValue.isEmpty){
        val formObjectType = (v \ "type").as[String]
        formObjectType match {
          case "DropdownSelect" =>
            k -> JsArray()
          case _ =>
            k -> JsNull
        }
      }else{
        k -> fieldValue.get
      }
    }
    val r = Json.toJson(m).as[JsObject].deepMerge(instance.content)
    instance.copy(content = Json.toJson(r).as[JsObject])
  }

  def removeEmptyFieldsNotInOriginal(originalInstance: NexusInstance, updates: EditorInstance): EditorInstance = {
    val contentUpdate = updates.contentToMap().filter {
      case (k, v) =>
        if (((v.asOpt[JsArray].isDefined && v.as[List[JsValue]].isEmpty) ||
          v == JsNull) &&
          (originalInstance.content \ k).isEmpty) {
          false
        } else if (v.asOpt[JsArray].isDefined && v.as[List[JsValue]].size == 1 &&
          (originalInstance.content \ k).asOpt[JsValue].isDefined &&
          v.as[List[JsValue]].head == (originalInstance.content \ k).as[JsValue]
        ) {
          false
        } else {
          true
        }
    }
    updates.copy(nexusInstance = updates.nexusInstance.copy(content = Json.toJson(contentUpdate).as[JsObject]))
  }

  def removeInternalFields(instance: NexusInstance): NexusInstance = {
    instance.copy(content = FormService.removeKey(instance.content).as[JsObject] -
      s"${EditorConstants.BASENAMESPACE}${EditorConstants.RELATIVEURL}" -
      s"${EditorConstants.INFERENCESPACE}${EditorConstants.ALTERNATIVES}"
    )
  }

}
