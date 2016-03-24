package models.daos.organization

import java.time.{Duration, LocalDateTime, ZoneId}

import anorm.SqlParser._
import anorm._
import models.daos.AbstractModelDAO
import models.{Campaign, Organization}

import scala.concurrent.Future

/**
  * Created by ryan on 3/9/16.
  */
class CampaignDAOImpl extends AbstractModelDAO[Campaign, Organization] {

  override protected def insert(campaign: Campaign): Future[Campaign] =
    insertWithParams(campaign)

  override protected def update(campaign: Campaign): Future[Campaign] =
    updateWithParams(campaign)

  override protected def getNamedParameters(campaign: Campaign): Option[List[NamedParameter]] = {
    campaign.org.idOpt map { campaignId =>
      List[NamedParameter](
        'org_id -> campaignId,
        'name -> campaign.name,
        'start_date -> campaign.startDate.atZone(ZoneId.systemDefault()),
        'end_date -> campaign.startDate.plus(campaign.duration).atZone(ZoneId.systemDefault())
      )
    }
  }

  override protected val insertSQL = SQL(
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

  override protected val selectSQL = SQL(
    """
      |select c.id, c.org_id, c.name, c.start_date, c.end_date, o.name
      |from campaign c
      |inner join organization o on c.org_id = o.id
      |where c.id = {id}
    """.stripMargin
  )

  override protected val selectAllSQL = SQL(
    """
      |select c.id, c.org_id, c.name, c.start_date, c.end_date, o.name
      |from campaign c
      |inner join organization o on c.org_id = o.id
    """.stripMargin
  )

  override protected val selectBySQL = SQL(
    """
      |select c.id, c.org_id, c.name, c.start_date, c.end_date, o.name
      |from campaign c
      |inner join organization o on c.org_id = o.id
      |where c.org_id = {org_id}
    """.stripMargin
  )

  override protected val parser = for {
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

  override protected val updateSQL = SQL(
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
