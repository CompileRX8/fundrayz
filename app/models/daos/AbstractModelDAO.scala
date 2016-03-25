package models.daos

import play.api.db.DB
import anorm._
import anorm.SqlParser._
import models.WithID
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play.current

import scala.concurrent.Future
import scala.reflect.ClassTag

/**
  * Created by ryan on 3/16/16.
  */
abstract class AbstractModelDAO[T <: WithID, FB <: WithID](implicit val tClassTag: ClassTag[T], implicit val fbClassTag: ClassTag[FB]) extends ModelDAO[T, FB] {

  private val tName = tClassTag.runtimeClass.getCanonicalName
  private val fbName = fbClassTag.runtimeClass.getCanonicalName

  def save(t: T): Future[T] =
    t.idOpt match {
      case None => insert(t)
      case Some(id) =>
        find(id) flatMap {
          case Some(_) => update(t)
          case None =>
            Future.failed(new IllegalStateException(s"Unable to update $tName when unable to find ID $id"))
        }
    }

  def insert(t: T): Future[T] = {
    getNamedParameters(t) map { namedParameters =>
      insert(insertSQL, namedParameters: _*)
    } getOrElse Future.failed(new IllegalStateException(s"Unable to insert $tName without named parameters"))
  }

  protected def insert(insertSQL: SqlQuery, insertParams: NamedParameter*): Future[T] =
    Future {
      DB.withConnection { implicit conn =>
        insertSQL
          .on(insertParams: _*)
          .executeInsert(scalar[Long].singleOpt)
      }
    } flatMap {
      case Some(id) =>
        find(id) map {
          _.get
        }
      case None =>
        Future.failed(new IllegalStateException(s"Unable to insert $tName with $insertParams"))
    }

  def update(t: T): Future[T] =
    t.getIDParam match {
      case None =>
        Future.failed(new IllegalStateException(s"Unable to update $tName without an ID"))
      case Some(idParam) =>
        val updateParamsOpt = getNamedParameters(t)
        updateParamsOpt map { updateParams =>
          val params = updateParams :+ idParam
          Future {
            DB.withConnection { implicit conn =>
              updateSQL.on(params: _*).executeUpdate()
            }
          } flatMap { _ =>
            find(t.idOpt.get)
          } map {
            _.get
          }
        } getOrElse Future.failed(new IllegalStateException(s"Unable to update $tName without a dependent ID"))
    }

  def find(id: Long): Future[Option[T]] = find(id, selectSQL, parser)

  protected def find(id: Long, selectSQL: SqlQuery, parser: RowParser[T]): Future[Option[T]] =
    Future {
      DB.withConnection { implicit conn =>
        selectSQL
          .on(
            'id -> id
          )
          .executeQuery()
          .as(parser.singleOpt)
      }
    }

  def findBy(fb: FB) = findBy(fb, selectBySQL, parser)

  protected def findBy(fb: FB, selectBySQL: SqlQuery, parser: RowParser[T]): Future[Option[List[T]]] =
    fb.idOpt match {
      case None =>
        Future.failed(new IllegalStateException(s"Unable to find ${tName}s by $fbName ID without an ID"))
      case Some(fbId) =>
        Future {
          DB.withConnection { implicit conn =>
            val fbIdSymbol = Symbol(selectBySQL.paramsInitialOrder.head)
            selectBySQL
              .on(
                fbIdSymbol -> fbId
              )
              .executeQuery()
              .as(parser.*)
          } match {
            case Nil => None
            case ts@List(_) => Some(ts)
          }
        }
    }

  def all: Future[List[T]] = all(selectSQL, parser)

  protected def all(selectSQL: SqlQuery, parser: RowParser[T]): Future[List[T]] =
    Future {
      DB.withConnection { implicit conn =>
        selectSQL
          .executeQuery()
          .as(parser.*)
      }
    }
}
