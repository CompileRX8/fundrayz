package models

import java.time.{Duration, LocalDateTime, ZoneId}
import java.util.Date

import anorm.NamedParameter

/**
  * Created by ryan on 3/24/16.
  */
trait WithDates {
  val startDate: LocalDateTime
  val duration: Duration
  def getDateNamedParameters(zoneId: ZoneId = ZoneId.systemDefault()): List[NamedParameter] =
    List[NamedParameter](
      'start_date -> startDate.atZone(zoneId),
      'end_date -> startDate.plus(duration).atZone(zoneId)
    )
}
object WithDates {
  def dateValues(start: Date, end: Date, zoneId: ZoneId = ZoneId.systemDefault()): WithDates = {
    new WithDates() {
      val startDate = LocalDateTime.from(start.toInstant).atZone(zoneId)
      val duration = Duration.between(start.toInstant, end.toInstant)
    }
  }
}