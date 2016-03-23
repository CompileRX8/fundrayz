package models.daos.organization

import java.time.{Duration, LocalDateTime, ZoneId}

import anorm.SqlParser._
import anorm._
import models.daos.ModelDAO
import models.{Campaign, Event}

import scala.concurrent.Future

/**
  * Created by ryan on 3/16/16.
  */
class EventDAOImpl extends ModelDAO[Event, Campaign] {
  import EventDAOImpl._

  override def save(event: Event): Future[Event] =
    save(event, event.id, insert, update)

  override protected def insert(event: Event): Future[Event] =
    insert(eventInsertSQL, eventIDParser,
      'campaign_id -> event.campaign.id,
      'name -> event.name,
      'start_date -> event.startDate.atZone(ZoneId.systemDefault()),
      'end_date -> event.startDate.plus(event.duration).atZone(ZoneId.systemDefault())
    )

  override protected def update(event: Event): Future[Event] = event.campaign.id match {
    case None => Future.failed(new IllegalStateException(s"Unable to update Event without a Campaign ID"))
    case Some(campaignId) => update(event.id, eventUpdateSQL,
      'campaign_id -> campaignId,
      'name -> event.name,
      'start_date -> event.startDate.atZone(ZoneId.systemDefault()),
      'end_date -> event.startDate.plus(event.duration).atZone(ZoneId.systemDefault())
    )
  }

  override def findBy(fb: Campaign): Future[Option[List[Event]]] =
    findBy(fb, fb.id, eventSelectByCampaignIDSQL, eventSelectParser)

  override def all: Future[List[Event]] = all(eventSelectAllSQL, eventSelectParser)

  override def find(id: Long): Future[Option[Event]] = find(id, eventSelectSQL, eventSelectParser)
}

object EventDAOImpl {
  val eventInsertSQL = SQL(
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
  val eventIDParser = long("id")

  private val eventSelect =
    """
      |select e.id, e.campaign_id, e.name, e.start_date, e.end_date
      |from event e
    """.stripMargin

  val eventSelectSQL = SQL(
    eventSelect +
    """
      |where e.id = {id}
    """.stripMargin
  )

  val eventSelectAllSQL = SQL(eventSelect)

  val eventSelectByCampaignIDSQL = SQL(
    eventSelect +
    """
      |where e.campaign_id = {campaign_id}
    """.stripMargin
  )

  val eventSelectParser = for {
    id <- long("e.id")
    campaignId <- long("e.campaign_id")
    name <- str("e.name")
    startDate <- date("e.start_date")
    endDate <- date("e.end_date")
  } yield {
    Event(
      Some(id),
      null,
      name,
      LocalDateTime.from(startDate.toInstant),
      Duration.between(startDate.toInstant, endDate.toInstant)
    )
  }

  val eventUpdateSQL = SQL(
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
