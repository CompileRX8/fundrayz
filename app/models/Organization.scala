package models

import java.time.{LocalDateTime, Duration}

/**
  * Created by ryan on 12/24/15.
  */
case class Organization(id: Option[Long], name: String)

case class Campaign(id: Option[Long], org: Organization, name: String, startDate: LocalDateTime, duration: Duration)

case class Event(id: Option[Long], campaign: Campaign, name: String, startDate: LocalDateTime, duration: Duration)

case class WorkSchedule(id: Option[Long], event: Event, user: User, startDate: LocalDateTime, duration: Duration)

case class Contact(id: Option[Long], userInfo: Option[User], orgs: List[Organization] = List())

case class Payment(id: Option[Long], payer: User, cashier: User, amount: BigDecimal, description: String)

case class Purchase(id: Option[Long], item: Item, purchaser: User, cashier: User, amount: BigDecimal)
