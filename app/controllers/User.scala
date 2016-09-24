package controllers

import javax.inject.Inject

import models.security.Auth0Config
import play.api.cache.CacheApi
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.JsValue
import play.api.mvc._

/**
  * Created by ryan on 4/2/16.
  */
class User @Inject()(cacheApi: CacheApi, auth0Config: Auth0Config, val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def login = Action { implicit request =>
    request.session.get("idToken") flatMap { idToken =>
      cacheApi.get[JsValue](idToken + "profile") map { profile =>
        Ok(views.html.user(profile))
      }
    } getOrElse Ok(views.html.login(auth0Config))
  }

  def logout = Action { implicit request =>
    request.session.get("idToken") foreach { idToken =>
      cacheApi.remove(idToken + "profile")
    }
    Ok(views.html.index()).removingFromSession("idToken", "accessToken")
  }
}
