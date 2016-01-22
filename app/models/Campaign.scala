package models

import java.time.{LocalDate, Duration}

/**
  * Created by ryan on 12/24/15.
  */
case class Campaign(id: Option[Int], org: Organization, name: String, startDate: LocalDate, duration: Duration)

object Campaign {

}