package models.daos.organization

import anorm.SqlParser._
import anorm._
import models.daos.AbstractModelDAO
import models.{Campaign, Organization, WithDates}
import play.api.libs.concurrent.Execution.Implicits._

/**
  * Created by ryan on 3/9/16.
  */
class CampaignDAOImpl extends AbstractModelDAO[Campaign, Organization] {

  override protected def getNamedParameters(campaign: Campaign): Option[List[NamedParameter]] = {
    campaign.org.idOpt map { orgId =>
      List[NamedParameter](
        'org_id -> orgId,
        'name -> campaign.name
      ) ++ campaign.getDateNamedParameters()
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

  private val selectString =
    """
      |select c.id, c.org_id, c.name, c.start_date, c.end_date, o.name
      |from campaign c
      |inner join organization o on c.org_id = o.id
    """.stripMargin

  override protected val selectSQL = SQL(
    selectString +
    """
      |where c.id = {id}
    """.stripMargin
  )

  override protected val selectAllSQL = SQL(selectString)

  override protected val selectBySQL = SQL(
    selectString +
    """
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
    withDates = WithDates.dateValues(startDate, endDate)
  } yield {
    Campaign(
      Some(id),
      Organization(Some(orgId), orgName),
      name,
      withDates.startDate,
      withDates.duration
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
