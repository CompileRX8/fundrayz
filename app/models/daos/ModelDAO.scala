package models.daos

import anorm.{NamedParameter, RowParser, SqlQuery}
import models.WithID

import scala.concurrent.Future

/**
  * Created by ryan on 3/23/16.
  */
trait ModelDAO[T <: WithID, FB <: WithID] {
  def save(t: T): Future[T]

  def find(id: Long): Future[Option[T]]

  def findBy(fb: FB): Future[Option[List[T]]]

  def all: Future[List[T]]

  protected def insert(t: T): Future[T]

  protected def update(t: T): Future[T]

  val selectAlias: String
  val selectString: String
  protected val selectSQL: SqlQuery
  protected val selectBySQL: SqlQuery
  protected val selectAllSQL: SqlQuery
  protected val insertSQL: SqlQuery
  protected val updateSQL: SqlQuery
  val parser: RowParser[T]
  protected def getNamedParameters(t: T): Option[List[NamedParameter]]
}
