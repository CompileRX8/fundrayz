package models.security

import play.api.libs.json.JsValue

/**
  * Created by ryan on 9/5/16.
  */
case class User(idOpt: Option[Long], idToken: String, firstName: Option[String], lastName: Option[String], email: Option[String], tags: Option[JsValue]) extends WithID
case class UserRole(id: Long, name: String, permissions: List[RolePermission] = List())
case class RolePermission(name: String)
