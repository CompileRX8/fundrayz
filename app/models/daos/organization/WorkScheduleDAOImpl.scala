package models.daos.organization

import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

import anorm.SqlParser._
import anorm._
import models.daos.AbstractModelDAO
import models.daos.security.UserDAO
import models.services.OrganizationService
import models.{Event, WithDates, WorkSchedule}
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.duration.Duration

/**
  * Created by ryan on 3/16/16.
  */
class WorkScheduleDAOImpl @Inject()(organizationService: OrganizationService, userDAO: UserDAO) extends AbstractModelDAO[WorkSchedule, Event] {

  private val selectString =
    """
      |select id, event_id, worker, start_date, end_date
      |from work_schedule
    """.stripMargin

  override protected val selectSQL: SqlQuery = SQL(
    selectString +
    """
      |where id = {id}
    """.stripMargin
  )

  override protected def getNamedParameters(t: WorkSchedule): Option[List[NamedParameter]] = {
    t.event.idOpt map { eventId =>
      List[NamedParameter](
        'event_id -> eventId,
        'worker -> t.user.userID
      ) ++ t.getDateNamedParameters()
    }
  }

  override protected val parser: RowParser[WorkSchedule] = {
    for {
      id <- long("id")
      eventId <- long("event_id")
      workerId <- get[UUID]("worker")
      startDate <- date("start_date")
      endDate <- date("end_date")
      withDates = WithDates.dateValues(startDate, endDate)
    } yield {
      organizationService.findEvent(eventId) zip userDAO.find(workerId) collect {
        case (Some(event), Some(worker)) =>
          WorkSchedule(
            Some(id),
            event,
            worker,
            withDates.startDate,
            withDates.duration
          )
      }
    }
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
      |  {worker}::uuid,
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
      |worker = {worker}::uuid,
      |start_date = {start_date},
      |end_date = {end_date}
      |where id = {id}
    """.stripMargin
  )

  override protected val selectBySQL: SqlQuery = SQL(
    selectString +
    """
      |where event_id = {event_id}
    """.stripMargin
  )
}
