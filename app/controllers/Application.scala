package controllers

import javax.inject.Inject

import models.security.Auth0Config
import play.api.cache.CacheApi
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.JsValue
import play.api.mvc._

/**
  * Created by ryan on 4/3/16.
  */
class Application @Inject()(auth0Config: Auth0Config, cacheApi: CacheApi, val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def AuthenticatedAction(f: Request[AnyContent] => Result): Action[AnyContent] = {
    Action { request =>
      (request.session.get("idToken").flatMap { idToken =>
        cacheApi.get[JsValue](idToken + "profile")
      } map { profile =>
        f(request)
      }).orElse {
        Some(Unauthorized)
      }.get
    }
  }

  def index = Action { implicit request =>
    request.session.get("idToken") flatMap { idToken =>
      cacheApi.get[JsValue](idToken + "profile") map { profile =>
        Ok(views.html.user(profile))
      }
    } getOrElse Ok(views.html.index())
  }
}
