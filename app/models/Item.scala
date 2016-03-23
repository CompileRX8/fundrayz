package models

import java.time.{LocalDateTime, Duration}

/**
  * Created by ryan on 3/8/16.
  */
case class Item(id: Option[Long], campaign: Campaign, itemNumber: String, name: String, description: String, estValue: BigDecimal)
case class Donation(id: Option[Long], item: Item, donor: String)

trait SalesType
case class OverTheCounter(price: BigDecimal, qtyOnHand: Int) extends SalesType
case class LiveAuction(minBid: BigDecimal = BigDecimal(0.0)) extends SalesType
case class SilentAuction(startDate: LocalDateTime, duration: Duration, minBid: BigDecimal = BigDecimal(0.0)) extends SalesType

case class Bid(id: Option[Long], item: Item, bidder: User, enteredBy: User, amount: BigDecimal)
