package models

import java.time.{LocalDateTime, Duration}

/**
  * Created by ryan on 3/8/16.
  */
case class Item(idOpt: Option[Long], event: Event, itemNumber: String, name: String, description: String, estValue: BigDecimal) extends WithID
case class Donation(idOpt: Option[Long], item: Item, donor: Either[String, User]) extends WithID

trait SalesType
case class OverTheCounter(price: BigDecimal, qtyOnHand: Int) extends SalesType
case class LiveAuction(minBid: BigDecimal = BigDecimal(0.0)) extends SalesType
case class SilentAuction(startDate: LocalDateTime, duration: Duration, minBid: BigDecimal = BigDecimal(0.0)) extends SalesType

case class Bid(idOpt: Option[Long], item: Item, bidder: User, enteredBy: User, amount: BigDecimal) extends WithID
