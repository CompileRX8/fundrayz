package models

import java.util.UUID

import com.mohiva.play.silhouette.api.{Authorization, Identity, LoginInfo}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import play.api.i18n.Messages
import play.api.mvc.Request

import scala.concurrent.Future

/**
  * The user object.
  *
  * @param userID The unique ID of the user.
  * @param loginInfo The linked login info.
  * @param firstName Maybe the first name of the authenticated user.
  * @param lastName Maybe the last name of the authenticated user.
  * @param fullName Maybe the full name of the authenticated user.
  * @param email Maybe the email of the authenticated provider.
  * @param avatarURL Maybe the avatar URL of the authenticated provider.
  */
case class User(
                 userID: UUID,
                 loginInfo: LoginInfo,
                 firstName: Option[String],
                 lastName: Option[String],
                 fullName: Option[String],
                 email: Option[String],
                 avatarURL: Option[String]
               ) extends Identity

//private case class UserRole(name: String)
//case object AdminRole extends UserRole("admin")
//case object UserRole extends UserRole("user")
//case object SuperUserRole extends UserRole("superuser")
//
//private case class UserPermission(name: String)
//case object CreatePermission extends UserPermission("create")
//case object UpdatePermission extends UserPermission("update")
//case object ReadPermission extends UserPermission("read")
//
//case object NameAuthz extends Authorization[User, CookieAuthenticator] {
//  override def isAuthorized[B](user: User, authenticator: CookieAuthenticator)(implicit request: Request[B], messages: Messages) = {
//    Future.successful(user.firstName.contains("Ryan"))
//  }
//}
