package models.daos.organization

import models.daos.ModelDAO
import models.{Campaign, Contact, Organization, Purchase}

import scala.concurrent.Future

/**
  * Created by ryan on 3/16/16.
  */
trait PurchaseDAO extends ModelDAO[Purchase, Contact] {
  def findBy(org: Organization): Future[Option[List[Purchase]]]
  def findBy(campaign: Campaign): Future[Option[List[Purchase]]]
}
