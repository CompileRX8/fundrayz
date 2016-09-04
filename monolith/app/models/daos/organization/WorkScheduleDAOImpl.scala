package models.daos.organization

import javax.inject.Inject

import anorm.SqlParser._
import anorm._
import models.daos.AbstractModelDAO
import models._
import play.api.Play.current

/**
  * Created by ryan on 3/16/16.
  */
class WorkScheduleDAOImpl @Inject()(eventDAO: AbstractModelDAO[Event, Organization]) extends AbstractModelDAO[WorkSchedule, Event] {

  override val selectAlias = "work"
  override val selectString =
    s"""
       |$selectAlias.id, $selectAlias.event_id, $selectAlias.worker, $selectAlias.start_date, $selectAlias.end_date,
       |${eventDAO.selectString}
       |inner join work_schedule $selectAlias on $selectAlias.event_id = ${eventDAO.selectAlias}.id
    """.stripMargin

  override protected val selectSQL: SqlQuery = SQL(
    s"""
       |select
       |$selectString
       |where $selectAlias.id = {id}
    """.stripMargin
  )

  override protected def getNamedParameters(t: WorkSchedule): Option[List[NamedParameter]] =
    t.event.idOpt map { eventId =>
    List[NamedParameter](
      'event_id -> eventId,
      'worker -> t.user.idToken
    ) ++ t.getDateNamedParameters()
  }

  override val parser: RowParser[WorkSchedule] = (
    for {
      id <- long(selectAlias + ".id")
      workerId <- str(selectAlias + ".worker")
      startDate <- date(selectAlias + ".start_date")
      endDate <- date(selectAlias + ".end_date")
      withDates = WithDates.dateValues(startDate, endDate)
    } yield {
      (WorkSchedule(
        Some(id),
        null,
        null,
        withDates.startDate,
        withDates.duration
      ), workerId)
    }) ~ eventDAO.parser map {
    case ~((ws: WorkSchedule, workerId: String), ev: Event) =>
      ws.copy(event = ev)
  }

  override protected val insertSQL: SqlQuery = SQL(
    """
      |insert into work_schedule (
      |  event_id,
      |  worker,
      |  start_date,
      |  end_date
      |) values (
      |  {event_id},
      |  {worker},
      |  {start_date},
      |  {end_date}
      |)
    """.stripMargin
  )

  override protected val selectAllSQL: SqlQuery = SQL(selectString)

  override protected val updateSQL: SqlQuery = SQL(
    """
      |update work_schedule
      |set
      |event_id = {event_id},
      |worker = {worker},
      |start_date = {start_date},
      |end_date = {end_date}
      |where id = {id}
    """.stripMargin
  )

  override protected val selectBySQL: SqlQuery = SQL(
    s"""
       |select
       |$selectString
       |where $selectAlias.event_id = {id}
    """.stripMargin
  )
}
