package models.daos.organization

import models.daos.ModelDAO
import models.{Contact, Organization}

import scala.concurrent.Future

/**
  * Created by ryan on 3/16/16.
  */
class ContactDAOImpl extends ModelDAO[Contact, Organization] {
  override def save(t: Contact): Future[Contact] = ???

  override def findBy(fb: Organization): Future[Option[List[Contact]]] = ???

  override def all: Future[List[Contact]] = ???

  override def find(id: Long): Future[Option[Contact]] = ???

  override protected def insert(t: Contact): Future[Contact] = ???

  override protected def update(t: Contact): Future[Contact] = ???
}
