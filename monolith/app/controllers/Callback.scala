package controllers

import javax.inject.Inject

import models.Auth0Config

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.Play
import play.api.Play.current
import play.api.cache.Cache
import play.api.http.HeaderNames
import play.api.http.MimeTypes
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.libs.ws.WS
import play.api.mvc.Action
import play.api.mvc.Controller

/**
  * Created by ryan on 4/2/16.
  */
class Callback @Inject()(auth0Config: Auth0Config) extends Controller {

  // callback route
  def callback(codeOpt: Option[String] = None) = Action.async {
    (for {
      code <- codeOpt
    } yield {
      // Get the token
      getToken(code).flatMap { case (idToken, accessToken) =>
        // Get the user
        getUser(accessToken).map { user =>
          // Cache the user and tokens into cache and session respectively
          Cache.set(idToken+ "profile", user)
          Redirect(routes.User.index())
            .withSession(
              "idToken" -> idToken,
              "accessToken" -> accessToken
            )
        }

      }.recover {
        case ex: IllegalStateException => Unauthorized(ex.getMessage)
      }
    }).getOrElse(Future.successful(BadRequest("No parameters supplied")))
  }

  def getToken(code: String): Future[(String, String)] = {
    val tokenResponse = WS.url(String.format("https://%s/oauth/token", auth0Config.domain))(Play.current).
      withHeaders(HeaderNames.ACCEPT -> MimeTypes.JSON).
      post(
        Json.obj(
          "client_id" -> auth0Config.clientId,
          "client_secret" -> auth0Config.secret,
          "redirect_uri" -> auth0Config.callbackURL,
          "code" -> code,
          "grant_type"-> "authorization_code"
        )
      )

    tokenResponse.flatMap { response =>
      (for {
        idToken <- (response.json \ "id_token").asOpt[String]
        accessToken <- (response.json \ "access_token").asOpt[String]
      } yield {
        Future.successful((idToken, accessToken))
      }).getOrElse(Future.failed[(String, String)](new IllegalStateException("Tokens not sent")))
    }

  }

  def getUser(accessToken: String): Future[JsValue] = {
    val userResponse = WS.url(String.format("https://%s/userinfo", auth0Config.domain))(Play.current)
      .withQueryString("access_token" -> accessToken)
      .get()

    userResponse.flatMap(response => Future.successful(response.json))
  }
}