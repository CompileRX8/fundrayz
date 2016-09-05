package controllers.security

import javax.inject.Inject

import models.security.Auth0Config
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}

/**
  * Created by ryan on 4/3/16.
  */
class Application @Inject()(auth0Config: Auth0Config, val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def index = Action {
    Ok(views.html.security.index(auth0Config))
  }
}