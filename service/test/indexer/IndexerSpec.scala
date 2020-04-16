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
package indexer

import java.io.FileInputStream

import org.scalatest.Assertion
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.test.Injecting
import services.indexer.IndexerImpl

import scala.concurrent.Await
import scala.concurrent.duration._

class IndexerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {
  override def fakeApplication() =
    GuiceApplicationBuilder()
      .configure(
        "play.http.filters"     -> "play.api.http.NoHttpFilters",
        "hbp.url"               -> "",
        "play.http.secret.key"  -> "123",
        "auth.refreshTokenFile" -> "test"
      )
      .build()

  def loadResource(filename: String): JsValue = {
    val jsonFile = getClass.getResource(filename).getFile
    val stream = new FileInputStream(jsonFile)
    Json.parse(stream)
  }

  def assertIsSameJsObject(
    fieldName: String,
    result: Map[String, JsValue],
    expected: Map[String, JsValue]
  ): Assertion = {
    assert(
      result(fieldName) == expected(fieldName)
    )
  }
  "The indexed trait" must {
    "create the labels properly" in {
      import monix.execution.Scheduler.Implicits.global
      val indexer = app.injector.instanceOf[IndexerImpl]
      val expected = loadResource("/indexer/labelsExpected.json").as[JsObject].value("Dataset").as[Map[String, JsValue]]
      val token =
        ""
      val task = indexer.createLabels(List("Dataset"), token)
      val labels = Await.result(task.runToFuture, Duration.Inf)
      val datasetLabels = labels("Dataset").as[Map[String, JsValue]]
//      assertIsSameJsObject("http://schema.org/identifier", datasetLabels, expected)
//      assertIsSameJsObject("http://schema.org/name", datasetLabels, expected)
//      assertIsSameJsObject("https://schema.hbp.eu/searchUi/boost", datasetLabels, expected)
//      assertIsSameJsObject("https://schema.hbp.eu/searchUi/defaultSelection", datasetLabels, expected)
//      assertIsSameJsObject("https://schema.hbp.eu/searchUi/icon", datasetLabels, expected)
//      assertIsSameJsObject("https://schema.hbp.eu/searchUi/order", datasetLabels, expected)
//      assertIsSameJsObject("https://schema.hbp.eu/searchUi/ribbon", datasetLabels, expected)
    }
  }

}
