package models.daos

import java.time.{Duration, LocalDateTime, ZoneId}
import javax.inject.Inject

import anorm.SqlParser._
import anorm._
import models.{Campaign, Organization}
import play.api.Play.current
import play.api.db.DB
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

/**
  * Created by ryan on 3/9/16.
  */
class CampaignDAOImpl @Inject()() extends CampaignDAO {

  import CampaignDAOImpl._

  override def find(id: Long): Future[Option[Campaign]] = Future {
    DB.withConnection { implicit conn =>
      campaignSelectSQL
        .on(
          'id -> id
        )
        .executeQuery()
        .as(campaignSelectParser.singleOpt)
    }
  }

  override def findByOrganization(org: Organization): Future[Option[List[Campaign]]] = org.id match {
    case None => Future.failed(new IllegalStateException(s"Unable to find Campaigns by Organization ID without an Organization ID"))
    case Some(orgId) => Future {
      DB.withConnection { implicit conn =>
        campaignSelectByOrgIDSQL
          .on(
            'org_id -> orgId
          )
          .executeQuery()
          .as(campaignSelectParser.*)
      } match {
        case Nil => None
        case cs@List(_) => Some(cs)
      }
    }
  }

  override def save(campaign: Campaign): Future[Campaign] = {
    campaign.id match {
      case None => insert(campaign)
      case Some(id) =>
        find(id) flatMap {
          case Some(_) => update(campaign)
          case None => Future.failed(new IllegalStateException(s"Unable to update Campaign when unable to find ID ${id}"))
        }
    }
  }

  private def insert(campaign: Campaign): Future[Campaign] = campaign.org.id match {
    case None => Future.failed(new IllegalStateException(s"Unable to insert Campaign without an Organization ID"))
    case Some(orgId) => Future {
      DB.withConnection { implicit conn =>
        campaignInsertSQL
          .on(
            'org_id -> orgId,
            'name -> campaign.name,
            'start_date -> campaign.startDate.atZone(ZoneId.systemDefault()),
            'end_date -> campaign.startDate.plus(campaign.duration).atZone(ZoneId.systemDefault())
          )
          .executeInsert(campaignIDParser.singleOpt)
      }
    } flatMap {
      case Some(id) =>
        find(id) map {
          _.get
        }
      case None => Future.failed(new IllegalStateException(s"Unable to insert Campaign with name ${campaign.name}"))
    }
  }

  private def update(campaign: Campaign): Future[Campaign] = (campaign.id, campaign.org.id) match {
    case (None, _) => Future.failed(new IllegalStateException(s"Unable to update Campaign without a Campaign ID"))
    case (_, None) => Future.failed(new IllegalStateException(s"Unable to update Campaign without an Organization ID"))
    case (Some(id), Some(orgId)) => Future {
      DB.withConnection { implicit conn =>
        campaignUpdateSQL
          .on(
            'org_id -> orgId,
            'name -> campaign.name,
            'start_date -> campaign.startDate.atZone(ZoneId.systemDefault()),
            'end_date -> campaign.startDate.plus(campaign.duration).atZone(ZoneId.systemDefault()),
            'id -> id
          )
          .executeUpdate()
      }
    } flatMap { _ =>
      find(campaign.id.get)
    } map {
      _.get
    }
  }
}

object CampaignDAOImpl {
  val campaignInsertSQL = SQL(
    """
      |insert into campaign (
      |  org_id,
      |  name,
      |  start_date,
      |  end_date
      |) values (
      |  {org_id},
      |  {name},
      |  {start_date},
      |  {end_date}
      |)
    """.stripMargin
  )
  val campaignIDParser = long("id")

  val campaignSelectSQL = SQL(
    """
      |select c.id, c.org_id, c.name, c.start_date, c.end_date, o.name
      |from campaign c
      |inner join organization o on c.org_id = o.id
      |where c.id = {id}
    """.stripMargin
  )

  val campaignSelectByOrgIDSQL = SQL(
    """
      |select c.id, c.org_id, c.name, c.start_date, c.end_date, o.name
      |from campaign c
      |inner join organization o on c.org_id = o.id
      |where c.org_id = {org_id}
    """.stripMargin
  )

  val campaignSelectParser = for {
    id <- long("c.id")
    orgId <- long("c.org_id")
    name <- str("c.name")
    startDate <- date("c.start_date")
    endDate <- date("c.end_date")
    orgName <- str("o.name")
  } yield {
    Campaign(
      Some(id),
      Organization(Some(orgId), orgName),
      name,
      LocalDateTime.from(startDate.toInstant),
      Duration.between(startDate.toInstant, endDate.toInstant)
    )
  }

  val campaignUpdateSQL = SQL(
    """
      |update campaign
      |set
      |org_id = {org_id},
      |name = {name},
      |start_date = {start_date},
      |end_date = {end_date}
      |where id = {id}
    """.stripMargin
  )
}
