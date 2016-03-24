package models.daos.organization

import anorm._
import models.daos.AbstractModelDAO
import models.{Campaign, Contact, Organization, Purchase}

import scala.concurrent.Future

/**
  * Created by ryan on 3/16/16.
  */
class PurchaseDAOImpl extends AbstractModelDAO[Purchase, Contact] with PurchaseDAO {
  override def findBy(org: Organization): Future[Option[List[Purchase]]] = ???

  override def findBy(campaign: Campaign): Future[Option[List[Purchase]]] = ???

  override protected def insert(t: Purchase): Future[Purchase] = ???

  override protected def update(t: Purchase): Future[Purchase] = ???

  override protected val selectSQL: SqlQuery = SQL("")

  override protected def getNamedParameters(t: Purchase): Option[List[NamedParameter]] = ???

  override protected val parser: RowParser[Purchase] = RowParser { _: Row => Success[Purchase](null) }
  override protected val insertSQL: SqlQuery = SQL("")
  override protected val selectAllSQL: SqlQuery = SQL("")
  override protected val updateSQL: SqlQuery = SQL("")
  override protected val selectBySQL: SqlQuery = SQL("")
}
