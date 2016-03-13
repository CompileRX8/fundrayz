package models

import java.time.{Duration, LocalDate}

/**
  * Created by ryan on 12/24/15.
  */
case class Event(id: Option[Int], campaign: Campaign, name: String, startDate: LocalDate, duration: Duration)

case class WorkSchedule(id: Option[Int], event: Event, user: User, startDate: LocalDate, duration: Duration)

object Event {
  private val nextEventId: Iterator[Int] = new Iterator[Int] {
    private var n = 1

    override def hasNext: Boolean = true

    override def next: Int = {
      val i = n
      n += 1
      i
    }
  }

  private val nextWorkScheduleId: Iterator[Int] = new Iterator[Int] {
    private var n = 1

    override def hasNext: Boolean = true

    override def next: Int = {
      val i = n
      n += 1
      i
    }
  }

  private var events: List[Event] = List()
  private var workSchedules: List[WorkSchedule] = List()

  def create(campaign: Campaign, name: String, startDate: LocalDate, duration: Duration): Event = {
    val event = Event(Some(nextEventId.next), campaign, name, startDate, duration)
    events :+= event
    event
  }

  def get(id: Int): Option[Event] = {
    events find {
      _.id.get == id
    }
  }

  def update(event: Event): Option[Event] = {
    event.id flatMap { id =>
      get(id) map { foundEvent =>
        val newEvent = Event(foundEvent.id, event.campaign, event.name, event.startDate, event.duration)
        val splitEvents = events splitAt (events indexWhere {
          _.id.get == id
        })
        events = (splitEvents._1 :+ newEvent) ++ splitEvents._2.tail
        newEvent
      }
    }
  }

  def delete(event: Event): Option[Event] = {
    event.id flatMap { id =>
      get(id) map { foundEvent =>
        val splitOrgs = events splitAt (events indexWhere {
          _.id.get == id
        })
        events = splitOrgs._1 ++ splitOrgs._2.tail
        foundEvent
      }
    }
  }

  def getWorkSchedules(event: Event): List[WorkSchedule] = {
    workSchedules filter { _.event == event }
  }

  private def workScheduleById(id: Int): (WorkSchedule) => Boolean = { workSchedule =>
    workSchedule.id.getOrElse(Int.MinValue) == id
  }

  def getWorkSchedule(id: Int): Option[WorkSchedule] = {
    workSchedules find workScheduleById(id)
  }

  def getEventsForCampaign(campaign: Campaign): List[Event] = {
    events filter { _.campaign == campaign }
  }

  def getWorkSchedulesForEvent(event: Event): List[WorkSchedule] = {
    workSchedules filter { _.event == event }
  }

  def addWorkSchedule(event: Event, user: User, startDate: LocalDate, duration: Duration): Option[WorkSchedule] = {
    event.id flatMap { id =>
      get(id) map { foundEvent =>
        val workSchedule = WorkSchedule(Some(nextWorkScheduleId.next), foundEvent, user, startDate, duration)
        workSchedules :+= workSchedule
        workSchedule
      }
    }
  }

  def updateWorkSchedule(workSchedule: WorkSchedule): Option[WorkSchedule] = {
    workSchedule.id flatMap { id =>
      getWorkSchedule(id) map { foundWorkSchedule =>
        val newWorkSchedule = WorkSchedule(Some(id), workSchedule.event, workSchedule.user, workSchedule.startDate, workSchedule.duration)
        val splitWorkSchedules = workSchedules splitAt (workSchedules indexWhere workScheduleById(id))
        workSchedules = (splitWorkSchedules._1 :+ newWorkSchedule) ++ splitWorkSchedules._2.tail
        newWorkSchedule
      }
    }
  }

  def removeWorkSchedule(workSchedule: WorkSchedule): Option[WorkSchedule] = {
    workSchedule.id flatMap { id =>
      getWorkSchedule(id) map { foundWorkSchedule =>
        val splitWorkSchedules = workSchedules splitAt (workSchedules indexWhere workScheduleById(id))
        workSchedules = splitWorkSchedules._1 ++ splitWorkSchedules._2.tail
        foundWorkSchedule
      }
    }
  }
}
