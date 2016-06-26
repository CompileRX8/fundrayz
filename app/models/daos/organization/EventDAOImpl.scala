package models.daos.organization

import javax.inject.Inject

import anorm.SqlParser._
import anorm._
import models.daos.AbstractModelDAO
import models.{Event, Organization, WithDates}
import play.api.Play.current

/**
  * Created by ryan on 3/16/16.
  */
class EventDAOImpl @Inject()(organizationDAO: AbstractModelDAO[Organization, Organization]) extends AbstractModelDAO[Event, Organization] {

  override protected def getNamedParameters(event: Event): Option[List[NamedParameter]] = {
    event.organization.idOpt map { orgId =>
      List[NamedParameter](
        'org_id -> orgId,
        'name -> event.name,
        'campaign -> event.campaign
      ) ++ event.getDateNamedParameters()
    }
  }

  override protected val insertSQL = SQL(
    """
      |insert into event (
      |  org_id,
      |  name,
      |  start_date,
      |  end_date,
      |  campaign
      |) values (
      |  {org_id},
      |  {name},
      |  {start_date},
      |  {end_date},
      |  {campaign}
      |)
    """.stripMargin
  )

  override val selectAlias = "ev"
  override val selectString =
    s"""
      |$selectAlias.id, $selectAlias.campaign_id, $selectAlias.name, $selectAlias.start_date, $selectAlias.end_date,
      |${organizationDAO.selectString}
      |inner join event $selectAlias on $selectAlias.org_id = ${organizationDAO.selectAlias}.id
    """.stripMargin

  override protected val selectSQL = SQL(
    s"""
      |select
      |$selectString
      |where $selectAlias.id = {id}
    """.stripMargin
  )

  override protected val selectAllSQL = SQL(s"select + $selectString")

  override protected val selectBySQL = SQL(
    s"""
       |select
       |$selectString
       |where $selectAlias.org_id = {id}
    """.stripMargin
  )

  override val parser = (for {
    id <- long(selectAlias + ".id")
    name <- str(selectAlias + ".name")
    startDate <- date(selectAlias + ".start_date")
    endDate <- date(selectAlias + ".end_date")
    campaign <- str(selectAlias + ".campaign").?
    withDates = WithDates.dateValues(startDate, endDate)
  } yield {
    Event(
      Some(id),
      null,
      name,
      withDates.startDate,
      withDates.duration,
      campaign
    )
  }) ~ organizationDAO.parser map {
    case ~(event: Event, org: Organization) =>
      event.copy(organization = org)
  }

  override protected val updateSQL = SQL(
    """
      |update event
      |set
      |org_id = {org_id},
      |name = {name},
      |start_date = {start_date},
      |end_date = {end_date},
      |campaign = {campaign}
      |where id = {id}
    """.stripMargin
  )
}
