package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import models.User
import play.api.i18n.MessagesApi

class OrganizationController @Inject()(val messagesApi: MessagesApi,
                                       val env: Environment[User, JWTAuthenticator])
  extends Silhouette[User, JWTAuthenticator] {

  def create(name: String) = SecuredAction { implicit request =>
    Ok
  }

  def createCampaign(orgId: Int, name: String) = SecuredAction { implicit request =>
    Ok
  }

  def createContact = SecuredAction { implicit request =>
    Ok
  }
}
