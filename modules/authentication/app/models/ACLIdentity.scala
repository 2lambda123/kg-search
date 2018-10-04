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
package authentication.models

import play.api.libs.json.{JsPath, Reads}

case class ACLIdentity(id: String, aclType: String, realm: Option[String], group: Option[String])

object ACLIdentity {

  import play.api.libs.functional.syntax._
  implicit val readIAMACL: Reads[ACLIdentity] = (
    (JsPath \ "@id").read[String] and
      (JsPath \ "@type").read[String] and
      (JsPath \ "realm").readNullable[String] and
      (JsPath \ "group").readNullable[String]
    )(ACLIdentity.apply _)
}