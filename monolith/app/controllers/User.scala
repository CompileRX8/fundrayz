package controllers

import javax.inject.Inject

import play.api.Play.current
import play.api.cache.Cache
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json._
import play.api.mvc._

/**
  * Created by ryan on 4/2/16.
  */
class User @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {
  def AuthenticatedAction(f: Request[AnyContent] => Result): Action[AnyContent] = {
    Action { request =>
      (request.session.get("idToken").flatMap { idToken =>
        Cache.getAs[JsValue](idToken + "profile")
      } map { profile =>
        f(request)
      }).orElse {
        Some(Redirect(routes.Application.index()))
      }.get
    }
  }

  def index = AuthenticatedAction { request =>
    val idToken = request.session.get("idToken").get
    val profile = Cache.getAs[JsValue](idToken + "profile").get
    Ok(views.html.user(profile))
  }
}
