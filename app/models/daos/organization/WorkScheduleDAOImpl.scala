package models.daos.organization

import anorm._
import models.daos.AbstractModelDAO
import models.{Event, WorkSchedule}

import scala.concurrent.Future

/**
  * Created by ryan on 3/16/16.
  */
class WorkScheduleDAOImpl extends AbstractModelDAO[WorkSchedule, Event] {
  override def save(t: WorkSchedule): Future[WorkSchedule] = ???

  override protected def insert(t: WorkSchedule): Future[WorkSchedule] = ???

  override protected def update(t: WorkSchedule): Future[WorkSchedule] = ???

  override def findBy(fb: Event): Future[Option[List[WorkSchedule]]] = ???

  override def all: Future[List[WorkSchedule]] = ???

  override def find(id: Long): Future[Option[WorkSchedule]] = ???

  override protected val selectSQL: SqlQuery = SQL("")

  override protected def getNamedParameters(t: WorkSchedule): Option[List[NamedParameter]] = ???

  override protected val parser: RowParser[WorkSchedule] = RowParser { _: Row => Success[WorkSchedule](null) }
  override protected val insertSQL: SqlQuery = SQL("")
  override protected val selectAllSQL: SqlQuery = SQL("")
  override protected val updateSQL: SqlQuery = SQL("")
  override protected val selectBySQL: SqlQuery = SQL("")
}
