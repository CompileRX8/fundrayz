package models.daos

import scala.concurrent.Future

/**
  * Created by ryan on 3/23/16.
  */
trait ModelDAOTrait[T, FB] {
  def save(t: T): Future[T]

  def find(id: Long): Future[Option[T]]

  def findBy(fb: FB): Future[Option[List[T]]]

  def all: Future[List[T]]

  protected def insert(t: T): Future[T]

  protected def update(t: T): Future[T]
}
