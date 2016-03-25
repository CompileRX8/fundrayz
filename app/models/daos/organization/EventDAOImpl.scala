package models.daos.organization

import javax.inject.Inject

import anorm.SqlParser._
import anorm._
import models.daos.AbstractModelDAO
import models.services.OrganizationService
import models.{Campaign, Event, WithDates}
import play.api.libs.concurrent.Execution.Implicits._

/**
  * Created by ryan on 3/16/16.
  */
class EventDAOImpl @Inject()(organizationService: OrganizationService) extends AbstractModelDAO[Event, Campaign] {

  override protected def getNamedParameters(event: Event): Option[List[NamedParameter]] = {
    event.campaign.idOpt map { campaignId =>
      List[NamedParameter](
        'campaign_id -> campaignId,
        'name -> event.name
      ) ++ event.getDateNamedParameters()
    }
  }

  override protected val insertSQL = SQL(
    """
      |insert into event (
      |  campaign_id,
      |  name,
      |  start_date,
      |  end_date
      |) values (
      |  {campaign_id},
      |  {name},
      |  {start_date},
      |  {end_date}
      |)
    """.stripMargin
  )

  private val selectString =
    """
      |select e.id, e.campaign_id, e.name, e.start_date, e.end_date
      |from event e
    """.stripMargin

  override protected val selectSQL = SQL(
    selectString +
    """
      |where e.id = {id}
    """.stripMargin
  )

  override protected val selectAllSQL = SQL(selectString)

  override protected val selectBySQL = SQL(
    selectString +
    """
      |where e.campaign_id = {campaign_id}
    """.stripMargin
  )

  override protected val parser = for {
    id <- long("e.id")
    campaignId <- long("e.campaign_id")
    campaignOpt <- organizationService.findCampaign(campaignId)
    campaign <- campaignOpt
    name <- str("e.name")
    startDate <- date("e.start_date")
    endDate <- date("e.end_date")
    withDates = WithDates.dateValues(startDate, endDate)
  } yield {
    Event(
      Some(id),
      campaign,
      name,
      withDates.startDate,
      withDates.duration
    )
  }

  override protected val updateSQL = SQL(
    """
      |update event
      |set
      |campaign_id = {campaign_id},
      |name = {name},
      |start_date = {start_date},
      |end_date = {end_date}
      |where id = {id}
    """.stripMargin
  )
}
