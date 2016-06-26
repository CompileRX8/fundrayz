package controllers

import java.time.{Duration, LocalDate}
import javax.inject.Inject

import play.api.i18n.MessagesApi
import play.api.mvc.{Action, Controller}

class Organization @Inject()(val messagesApi: MessagesApi) extends Controller {

  def create(name: String) = Action { implicit request =>
    Ok
  }

  def getAll() = Action { implicit request =>
    Ok
  }

  def createCampaign(orgId: Int, name: String, startDate: LocalDate, duration: Duration) = Action { implicit request =>
    Ok
  }

  def getCampaigns(orgId: Int) = Action { implicit request =>
    Ok
  }

  def createContact = Action { implicit request =>
    Ok
  }
}
