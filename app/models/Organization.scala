package models

import java.time.{LocalDateTime, Duration}

/**
  * Created by ryan on 12/24/15.
  */
case class Organization(id: Option[Long], name: String) extends WithID(id)

case class Campaign(id: Option[Long], org: Organization, name: String, startDate: LocalDateTime, duration: Duration) extends WithID(id)

case class Event(id: Option[Long], campaign: Campaign, name: String, startDate: LocalDateTime, duration: Duration) extends WithID(id)

case class WorkSchedule(id: Option[Long], event: Event, user: User, startDate: LocalDateTime, duration: Duration) extends WithID(id)

case class Contact(id: Option[Long], userInfo: Option[User], orgs: List[Organization] = List()) extends WithID(id)

case class Payment(id: Option[Long], payer: User, cashier: User, amount: BigDecimal, description: String) extends WithID(id)

case class Purchase(id: Option[Long], item: Item, purchaser: User, cashier: User, amount: BigDecimal) extends WithID(id)
