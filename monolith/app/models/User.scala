package models

import play.api.libs.json.JsValue

case class User(idOpt: Option[Long], idToken: String, firstName: Option[String], lastName: Option[String], email: Option[String], tags: Option[JsValue]) extends WithID
case class UserRole(id: Long, name: String, permissions: List[RolePermission] = List())
case class RolePermission(name: String)
