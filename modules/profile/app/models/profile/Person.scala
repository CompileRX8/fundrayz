package models.profile

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Person(id: Option[Long], name: String, content: JsValue)

object Person {
  
  implicit val personReads: Reads[Person] = (
    (JsPath \ "id").readNullable[Long] and
    (JsPath \ "name").read[String] and
    JsPath.read[JsValue]
    )(Person.apply _)

  implicit val personWrites: Writes[Person] = (
    (JsPath \ "id").writeNullable[Long] and
    (JsPath \ "name").write[String] and
    JsPath.write[JsValue]
    )(unlift(Person.unapply))
}
