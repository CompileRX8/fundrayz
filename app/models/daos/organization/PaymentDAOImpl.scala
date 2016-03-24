package models.daos.organization

import anorm._
import models.daos.AbstractModelDAO
import models.{Contact, Organization, Payment}

import scala.concurrent.Future

/**
  * Created by ryan on 3/16/16.
  */
class PaymentDAOImpl extends AbstractModelDAO[Payment, Contact] with PaymentDAO {
  override def findBy(org: Organization): Future[Option[List[Payment]]] = ???

  override def findBy(fb: Contact): Future[Option[List[Payment]]] = ???

  override def all: Future[List[Payment]] = ???

  override def find(id: Long): Future[Option[Payment]] = ???

  override protected def insert(t: Payment): Future[Payment] = ???

  override protected def update(t: Payment): Future[Payment] = ???

  override protected val selectSQL: SqlQuery = SQL("")

  override protected def getNamedParameters(t: Payment): Option[List[NamedParameter]] = ???

  override protected val parser: RowParser[Payment] = RowParser { _: Row => Success[Payment](null) }
  override protected val insertSQL: SqlQuery = SQL("")
  override protected val selectAllSQL: SqlQuery = SQL("")
  override protected val updateSQL: SqlQuery = SQL("")
  override protected val selectBySQL: SqlQuery = SQL("")
}
