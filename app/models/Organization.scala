package models

import java.time.{LocalDateTime, Duration}

/**
  * Created by ryan on 12/24/15.
  */
case class Organization(idOpt: Option[Long], name: String) extends WithID

case class Campaign(idOpt: Option[Long], org: Organization, name: String, startDate: LocalDateTime, duration: Duration) extends WithID with WithDates

case class Event(idOpt: Option[Long], campaign: Campaign, name: String, startDate: LocalDateTime, duration: Duration) extends WithID with WithDates

case class WorkSchedule(idOpt: Option[Long], event: Event, user: User, startDate: LocalDateTime, duration: Duration) extends WithID with WithDates

case class Contact(idOpt: Option[Long], person: User, orgs: List[Organization] = List()) extends WithID

case class Payment(idOpt: Option[Long], payer: User, cashier: User, amount: BigDecimal, description: String) extends WithID

case class Purchase(idOpt: Option[Long], item: Item, purchaser: User, cashier: User, amount: BigDecimal) extends WithID
