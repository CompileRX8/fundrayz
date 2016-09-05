package controllers

import javax.inject.Inject

import models.Auth0Config
import play.api.{Configuration, Logger}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.cache.CacheApi
import play.api.http.HeaderNames
import play.api.http.MimeTypes
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.mvc._
import play.api.libs.ws._

class Callback @Inject()(ws: WSClient, cacheApi: CacheApi, config: Configuration, auth0Config: Auth0Config, val messagesApi: MessagesApi) extends Controller with I18nSupport {

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
          cacheApi.set(idToken+ "profile", user)
          Redirect(routes.User.index()).withSession(
            "idToken" -> idToken,
            "accessToken" -> accessToken
          )
        }

      }.recover {
        case ex: IllegalStateException => Unauthorized(ex.getMessage)
      }
    }).getOrElse(Future.successful(BadRequest(messagesApi("callback.no.parameters.supplied"))))
  }

  def getToken(code: String): Future[(String, String)] = {
    val tokenResponse = ws.url(String.format("https://%s/oauth/token", auth0Config.domain)).
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
      }).getOrElse(Future.failed[(String, String)](new IllegalStateException(messagesApi("gettoken.tokens.not.sent"))))
    }

  }

  def getUser(accessToken: String): Future[JsValue] = {
    val userResponse = ws.url(String.format("https://%s/userinfo", auth0Config.domain))
      .withQueryString("access_token" -> accessToken)
      .get()

    userResponse.flatMap(response => Future.successful(response.json))
  }
}