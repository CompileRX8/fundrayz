package models.daos.organization

import models.{Contact, Organization, Payment}

import scala.concurrent.Future

/**
  * Created by ryan on 3/16/16.
  */
class PaymentDAOImpl extends PaymentDAO {
  override def findBy(org: Organization): Future[Option[List[Payment]]] = ???

  override def findBy(fb: Contact): Future[Option[List[Payment]]] = ???

  override def all: Future[List[Payment]] = ???

  override def save(t: Payment): Future[Payment] = ???

  override def find(id: Long): Future[Option[Payment]] = ???

  override protected def insert(t: Payment): Future[Payment] = ???

  override protected def update(t: Payment): Future[Payment] = ???
}
