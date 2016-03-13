package models

import java.util.UUID

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}

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

class UserRole(val name: String)
trait OrgRole
trait CampaignRole
trait EventRole
case object OrgAdmin extends UserRole("orgadmin") with OrgRole
case object CampaignAdmin extends UserRole("campaignadmin") with CampaignRole
case object EventAdmin extends UserRole("eventadmin") with EventRole
case object PaymentAdmin extends UserRole("paymentadmin") with OrgRole
case object SuperAdmin extends UserRole("superadmin")

class Permission(val name: String)
case object CreateCampaign extends Permission("createcampaign")
case object UpdateCampaign extends Permission("updatecampaign")
case object CreateEvent extends Permission("createevent")
case object UpdateEvent extends Permission("updateevent")
case object CreateItem extends Permission("createitem")
case object UpdateItem extends Permission("updateitem")

//case object NameAuthz extends Authorization[User, CookieAuthenticator] {
//  override def isAuthorized[B](user: User, authenticator: CookieAuthenticator)(implicit request: Request[B], messages: Messages) = {
//    Future.successful(user.firstName.contains("Ryan"))
//  }
//}
