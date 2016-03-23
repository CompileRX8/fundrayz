package models.daos.organization

import java.time.{Duration, LocalDateTime, ZoneId}

import anorm.SqlParser._
import anorm._
import models.daos.ModelDAO
import models.{Campaign, Organization}

import scala.concurrent.Future

/**
  * Created by ryan on 3/9/16.
  */
class CampaignDAOImpl extends ModelDAO[Campaign, Organization] {

  import CampaignDAOImpl._

  override def find(id: Long): Future[Option[Campaign]] =
    find(id, campaignSelectSQL, campaignSelectParser)

  override def findBy(org: Organization): Future[Option[List[Campaign]]] =
    findBy(org, org.id, campaignSelectByOrgIDSQL, campaignSelectParser)

  override def save(campaign: Campaign): Future[Campaign] =
    save(campaign, campaign.id, insert, update)

  override protected def insert(campaign: Campaign): Future[Campaign] =
    insert(campaignInsertSQL, campaignIDParser,
      'org_id -> campaign.org.id,
      'name -> campaign.name,
      'start_date -> campaign.startDate.atZone(ZoneId.systemDefault()),
      'end_date -> campaign.startDate.plus(campaign.duration).atZone(ZoneId.systemDefault())
    )

  override protected def update(campaign: Campaign): Future[Campaign] = campaign.org.id match {
    case None => Future.failed(new IllegalStateException(s"Unable to update Campaign without an Organization ID"))
    case Some(orgId) => update(campaign.id, campaignUpdateSQL,
      'org_id -> orgId,
      'name -> campaign.name,
      'start_date -> campaign.startDate.atZone(ZoneId.systemDefault()),
      'end_date -> campaign.startDate.plus(campaign.duration).atZone(ZoneId.systemDefault())
    )
  }

  override def all: Future[List[Campaign]] =
    all(campaignSelectAllSQL, campaignSelectParser)
}

object CampaignDAOImpl {
  val campaignInsertSQL = SQL(
    """
      |insert into campaign (
      |  org_id,
      |  name,
      |  start_date,
      |  end_date
      |) values (
      |  {org_id},
      |  {name},
      |  {start_date},
      |  {end_date}
      |)
    """.stripMargin
  )
  val campaignIDParser = long("id")

  val campaignSelectSQL = SQL(
    """
      |select c.id, c.org_id, c.name, c.start_date, c.end_date, o.name
      |from campaign c
      |inner join organization o on c.org_id = o.id
      |where c.id = {id}
    """.stripMargin
  )

  val campaignSelectAllSQL = SQL(
    """
      |select c.id, c.org_id, c.name, c.start_date, c.end_date, o.name
      |from campaign c
      |inner join organization o on c.org_id = o.id
    """.stripMargin
  )

  val campaignSelectByOrgIDSQL = SQL(
    """
      |select c.id, c.org_id, c.name, c.start_date, c.end_date, o.name
      |from campaign c
      |inner join organization o on c.org_id = o.id
      |where c.org_id = {org_id}
    """.stripMargin
  )

  val campaignSelectParser = for {
    id <- long("c.id")
    orgId <- long("c.org_id")
    name <- str("c.name")
    startDate <- date("c.start_date")
    endDate <- date("c.end_date")
    orgName <- str("o.name")
  } yield {
    Campaign(
      Some(id),
      Organization(Some(orgId), orgName),
      name,
      LocalDateTime.from(startDate.toInstant),
      Duration.between(startDate.toInstant, endDate.toInstant)
    )
  }

  val campaignUpdateSQL = SQL(
    """
      |update campaign
      |set
      |org_id = {org_id},
      |name = {name},
      |start_date = {start_date},
      |end_date = {end_date}
      |where id = {id}
    """.stripMargin
  )
}
