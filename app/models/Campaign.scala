package models

import java.time.{LocalDateTime, LocalDate, Duration}

/**
  * Created by ryan on 12/24/15.
  */
case class Campaign(id: Option[Long], org: Organization, name: String, startDate: LocalDateTime, duration: Duration)

object Campaign {
  private val nextCampaignId: Iterator[Long] = new Iterator[Long] {
    private var n = 1L

    override def hasNext: Boolean = true

    override def next: Long = {
      val i = n
      n += 1
      i
    }
  }

  private var campaigns: List[Campaign] = List()

  def create(org: Organization, name: String, startDate: LocalDateTime, duration: Duration): Campaign = {
    val campaign = Campaign(Some(nextCampaignId.next), org, name, startDate, duration)
    campaigns :+= campaign
    campaign
  }

  def get(id: Long): Option[Campaign] = {
    campaigns find {
      _.id.get == id
    }
  }

  def getCampaignsForOrganization(org: Organization): List[Campaign] = {
    campaigns filter { _.org == org }
  }

  def update(campaign: Campaign): Option[Campaign] = {
    campaign.id flatMap { id =>
      get(id) map { foundCampaign =>
        val newCampaign = Campaign(foundCampaign.id, campaign.org, campaign.name, campaign.startDate, campaign.duration)
        val splitCampaigns = campaigns splitAt (campaigns indexWhere {
          _.id.get == id
        })
        campaigns = (splitCampaigns._1 :+ newCampaign) ++ splitCampaigns._2.tail
        newCampaign
      }
    }
  }

  def delete(campaign: Campaign): Option[Campaign] = {
    campaign.id flatMap { id =>
      get(id) map { foundCampaign =>
        val splitOrgs = campaigns splitAt (campaigns indexWhere {
          _.id.get == id
        })
        campaigns = splitOrgs._1 ++ splitOrgs._2.tail
        foundCampaign
      }
    }
  }
}