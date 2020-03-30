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
package models.templates.entities

import play.api.libs.json.{JsNull, JsValue, Json, Writes}

case class ValueObjectString(value: Option[String]) extends TemplateEntity {
  override type T = ValueObjectString

  def map[B](t: String => String): ValueObjectString = {
    ValueObjectString(value.map(t))
  }
  override def toJson: JsValue = Json.toJson(this)(ValueObjectString.implicitWrites)

  override def zero: ValueObjectString = ValueObjectString.zero
}

object ValueObjectString {
  implicit val implicitWrites = new Writes[ValueObjectString] {

    def writes(u: ValueObjectString): JsValue = {
      u.value.fold[JsValue](JsNull)(str => Json.obj("value" -> str))
    }
  }
  def zero: ValueObjectString = ValueObjectString(None)
}
