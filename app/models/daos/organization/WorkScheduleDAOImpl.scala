package models.daos.organization

import models.daos.ModelDAO
import models.{Event, WorkSchedule}

import scala.concurrent.Future

/**
  * Created by ryan on 3/16/16.
  */
class WorkScheduleDAOImpl extends ModelDAO[WorkSchedule, Event] {
  override def save(t: WorkSchedule): Future[WorkSchedule] = ???

  override protected def insert(t: WorkSchedule): Future[WorkSchedule] = ???

  override protected def update(t: WorkSchedule): Future[WorkSchedule] = ???

  override def findBy(fb: Event): Future[Option[List[WorkSchedule]]] = ???

  override def all: Future[List[WorkSchedule]] = ???

  override def find(id: Long): Future[Option[WorkSchedule]] = ???
}
