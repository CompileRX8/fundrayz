package controllers

import javax.inject.Inject

import models.Auth0Config
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}

/**
  * Created by ryan on 4/3/16.
  */
class Application @Inject()(auth0Config: Auth0Config, val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def index = Action {
    Ok(views.html.index(auth0Config))
  }
}
