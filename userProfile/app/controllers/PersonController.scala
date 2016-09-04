package controllers

import javax.inject._

import dal._
import models._
import play.api.Logger
import play.api.data.validation.ValidationError
import play.api.i18n._
import play.api.libs.json.{JsError, JsPath, JsValue, Json}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class PersonController @Inject() (repo: PersonRepository, val messagesApi: MessagesApi)
                                 (implicit ec: ExecutionContext) extends Controller with I18nSupport{

  private val invalidPersonResult = (errors: Seq[(JsPath, Seq[ValidationError])]) => Future.successful {
    val errorJson = Json.obj(
      Messages("person.parse.error.status.key") -> Messages("person.parse.error.status.value"),
      Messages("person.parse.error.message.key") -> JsError.toJson(errors)
    )
    Logger.warn("Invalid person: " + errorJson)
    BadRequest(errorJson)
  }

  private val validPersonResult = (status: Status) => (person: Option[Person]) => {
    val personJson = Json.toJson(person)
    Logger.debug("Valid person: " + personJson)
    status(personJson)
  }

  private def handleJsonRequest(personFn: (Person) => Future[Result]): Action[JsValue] =
    Action.async(BodyParsers.parse.json) { implicit request =>
      val personResult = request.body.validate[Person]
      Logger.debug("personResult: " + personResult)
      personResult.fold(invalidPersonResult, personFn)
    }

  def addPerson = handleJsonRequest {
    person => {
      Logger.debug("Adding person: " + person)
      repo.create(person.name, person.content).map { newPerson =>
        Logger.debug("Success: " + newPerson)
        Created(Json.toJson(newPerson))
      }
    }
  }

  def getPerson(id: Long) = Action.async {
    repo.get(id).map(validPersonResult(Ok))
  }

  def updatePerson(id: Long) = handleJsonRequest {
    person => {
      val p = Person(Some(id), person.name, person.content)
      Logger.debug("Updating person: " + p)
      repo.update(p).map(validPersonResult(Accepted))
    }
  }

  def deletePerson(id: Long) = Action.async {
    repo.delete(id).map(validPersonResult(Ok))
  }

  def getPersons = Action.async {
  	repo.list().map { people =>
      Ok(Json.toJson(people))
    }
  }
}
