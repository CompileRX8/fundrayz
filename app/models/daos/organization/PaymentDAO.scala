package models.daos.organization

import models.daos.ModelDAO
import models.{Contact, Organization, Payment}

import scala.concurrent.Future

/**
  * Created by ryan on 3/16/16.
  */
trait PaymentDAO extends ModelDAO[Payment, Contact] {
  def findBy(org: Organization): Future[Option[List[Payment]]]
}
