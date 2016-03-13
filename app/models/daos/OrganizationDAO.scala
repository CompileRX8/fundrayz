package models.daos

import models.Organization

import scala.concurrent.Future

/**
  * Created by ryan on 3/10/16.
  */
trait OrganizationDAO {
  def find(id: Long): Future[Option[Organization]]

  def save(org: Organization): Future[Organization]
}
