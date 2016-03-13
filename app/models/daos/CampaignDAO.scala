package models.daos

import models.{Organization, Campaign}

import scala.concurrent.Future

/**
  * Created by ryan on 3/10/16.
  */
trait CampaignDAO {
  def find(id: Long): Future[Option[Campaign]]
  def save(campaign: Campaign): Future[Campaign]
  def findByOrganization(org: Organization): Future[Option[List[Campaign]]]
}
