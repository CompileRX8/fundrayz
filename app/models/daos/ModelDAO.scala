package models.daos

import play.api.db.DB
import anorm._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play.current

import scala.concurrent.Future
import scala.reflect.ClassTag

/**
  * Created by ryan on 3/16/16.
  */
abstract class ModelDAO[T, FB](implicit val tClassTag: ClassTag[T], implicit val fbClassTag: ClassTag[FB]) extends ModelDAOTrait[T, FB] {

  protected def save(t: T, tOptID: Option[Long], insert: (T) => Future[T], update: (T) => Future[T]): Future[T] = {
    tOptID match {
      case None => insert(t)
      case Some(id) =>
        find(id) flatMap {
          case Some(_) => update(t)
          case None =>
            val tName = tClassTag.runtimeClass.getCanonicalName
            Future.failed(new IllegalStateException(s"Unable to update $tName when unable to find ID $id"))
        }
    }
  }

  protected def insert(insertSQL: SqlQuery, parser: RowParser[Long], insertParams: NamedParameter*): Future[T] = Future {
    DB.withConnection { implicit conn =>
      insertSQL
        .on(insertParams:_*)
        .executeInsert(parser.singleOpt)
    }
  } flatMap {
    case Some(id) =>
      find(id) map { _.get }
    case None =>
      val tName = tClassTag.runtimeClass.getCanonicalName
      Future.failed(new IllegalStateException(s"Unable to insert $tName with $insertParams"))
  }

  protected def update(tOptID: Option[Long], updateSQL: SqlQuery, updateParams: NamedParameter*): Future[T] = {
    tOptID match {
      case None =>
        val tName = tClassTag.runtimeClass.getCanonicalName
        Future.failed(new IllegalStateException(s"Unable to update $tName without an ID"))
      case Some(id) => Future {
        DB.withConnection { implicit conn =>
          val idParam: NamedParameter = 'id -> id
          val params = updateParams :+ idParam
          updateSQL.on(params:_*).executeUpdate()
        }
      } flatMap { _ =>
        find(tOptID.get)
      } map { _.get }
    }
  }

  protected def find(id: Long, selectSQL: SqlQuery, parser: RowParser[T]): Future[Option[T]] = Future {
    DB.withConnection { implicit conn =>
      selectSQL
        .on(
          'id -> id
        )
        .executeQuery()
        .as(parser.singleOpt)
    }
  }

  protected def findBy(fb: FB, optFbId: Option[Long], selectBySQL: SqlQuery, parser: RowParser[T]): Future[Option[List[T]]] = optFbId match {
    case None =>
      val tName = tClassTag.runtimeClass.getCanonicalName
      val fbName = fbClassTag.runtimeClass.getCanonicalName
      Future.failed(new IllegalStateException(s"Unable to find ${tName}s by $fbName ID without an ID"))
    case Some(fbId) => Future {
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

  protected def all(selectSQL: SqlQuery, parser: RowParser[T]): Future[List[T]] = Future {
    DB.withConnection { implicit conn =>
      selectSQL
        .executeQuery()
        .as(parser.*)
    }
  }
}
