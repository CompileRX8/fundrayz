package models

import java.time.{Duration, LocalDate}

/**
  * Created by ryan on 3/8/16.
  */
case class Item(id: Option[Int], campaign: Campaign, itemNumber: String, name: String, description: String, estValue: BigDecimal)
case class Donation(id: Option[Int], item: Item, donor: String)

trait SalesType
case class OverTheCounter(price: BigDecimal) extends SalesType
case class LiveAuction(minBid: BigDecimal = BigDecimal(0.0)) extends SalesType
case class SilentAuction(startDate: LocalDate, duration: Duration, minBid: BigDecimal = BigDecimal(0.0)) extends SalesType

object Item {
  private val nextItemId: Iterator[Int] = new Iterator[Int] {
    private var n = 1

    override def hasNext: Boolean = true

    override def next: Int = {
      val i = n
      n += 1
      i
    }
  }

  private val nextDonationId: Iterator[Int] = new Iterator[Int] {
    private var n = 1

    override def hasNext: Boolean = true

    override def next: Int = {
      val i = n
      n += 1
      i
    }
  }

  private var items: List[Item] = List()
  private var donations: List[Donation] = List()

  private def itemById(id: Int): (Item) => Boolean = { item =>
    item.id.getOrElse(Int.MinValue) == id
  }
  private def donationById(id: Int): (Donation) => Boolean = { donation =>
    donation.id.getOrElse(Int.MinValue) == id
  }

  def create(campaign: Campaign, itemNumber: String, name: String, description: String = "", estValue: BigDecimal = BigDecimal(0.0)): Item = {
    val item = Item(Some(nextItemId.next), campaign, itemNumber, name, description, estValue)
    items :+= item
    item
  }

  def get(id: Int): Option[Item] = {
    items find itemById(id)
  }

  def getDonation(id: Int): Option[Donation] = {
    donations find donationById(id)
  }

  def getDonationForItem(item: Item): Option[Donation] = {
    donations find { _.item == item }
  }

  def addDonor(itemId: Int, donor: String): Option[Donation] = {
    get(itemId) map { item =>
      val donation = Donation(Some(nextDonationId.next), item, donor)
      donations :+= donation
      donation
    }
  }

  def updateDonor(donation: Donation): Option[Donation] = {
    donation.id flatMap { id =>
      getDonation(id) map { foundDonation =>
        val newDonation = Donation(foundDonation.id, donation.item, donation.donor)
        val splitDonations = donations splitAt (donations indexWhere donationById(id))
        donations = (splitDonations._1 :+ newDonation) ++ splitDonations._2.tail
        newDonation
      }
    }
  }

  def removeDonor(donation: Donation): Option[Donation] = {
    donation.id flatMap { id =>
      getDonation(id) map { foundDonation =>
        val splitDonations = donations splitAt (donations indexWhere donationById(id))
        donations = splitDonations._1 ++ splitDonations._2.tail
        foundDonation
      }
    }
  }

  def update(item: Item): Option[Item] = {
    item.id flatMap { id =>
      get(id) map { foundItem =>
        val newItem = Item(Some(id), item.campaign, item.itemNumber, item.name, item.description, item.estValue)
        val splitItems = items splitAt (items indexWhere itemById(id))
        items = (splitItems._1 :+ newItem) ++ splitItems._2.tail
        newItem
      }
    }
  }

  def delete(item: Item): Option[Item] = {
    item.id flatMap { id =>
      get(id) map { foundItem =>
        val splitItems = items splitAt (items indexWhere itemById(id))
        items = splitItems._1 ++ splitItems._2.tail
        foundItem
      }
    }
  }
}
