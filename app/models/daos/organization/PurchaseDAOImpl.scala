package models.daos.organization

import models.{Campaign, Contact, Organization, Purchase}

import scala.concurrent.Future

/**
  * Created by ryan on 3/16/16.
  */
class PurchaseDAOImpl extends PurchaseDAO {
  override def findBy(org: Organization): Future[Option[List[Purchase]]] = ???

  override def findBy(campaign: Campaign): Future[Option[List[Purchase]]] = ???

  override def findBy(fb: Contact): Future[Option[List[Purchase]]] = ???

  override def all: Future[List[Purchase]] = ???

  override def save(t: Purchase): Future[Purchase] = ???

  override def find(id: Long): Future[Option[Purchase]] = ???

  override protected def insert(t: Purchase): Future[Purchase] = ???

  override protected def update(t: Purchase): Future[Purchase] = ???
}
