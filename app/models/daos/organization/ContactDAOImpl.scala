package models.daos.organization

import anorm._
import models.daos.AbstractModelDAO
import models.{Contact, Organization}

import scala.concurrent.Future

/**
  * Created by ryan on 3/16/16.
  */
class ContactDAOImpl extends AbstractModelDAO[Contact, Organization] {
  override def findBy(fb: Organization): Future[Option[List[Contact]]] = ???

  override def all: Future[List[Contact]] = ???

  override def find(id: Long): Future[Option[Contact]] = ???

  override protected def insert(t: Contact): Future[Contact] = ???

  override protected def update(t: Contact): Future[Contact] = ???

  override protected val selectSQL: SqlQuery = SQL("")

  override protected def getNamedParameters(t: Contact): Option[List[NamedParameter]] = ???

  override protected val parser: RowParser[Contact] = RowParser { _: Row => Success(Contact(None, None)) }
  override protected val insertSQL: SqlQuery = SQL("")
  override protected val selectAllSQL: SqlQuery = SQL("")
  override protected val updateSQL: SqlQuery = SQL("")
  override protected val selectBySQL: SqlQuery = SQL("")
}
