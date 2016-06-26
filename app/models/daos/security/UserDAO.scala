package models.daos.security

import models.User
import models.daos.ModelDAO

import scala.concurrent.Future

/**
  * Created by ryan on 4/11/16.
  */
trait UserDAO extends ModelDAO[User, User] {
  def findBy(idToken: String): Future[Option[User]]
}
