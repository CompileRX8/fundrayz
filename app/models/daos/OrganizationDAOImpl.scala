package models.daos

import javax.inject.Inject

import models.Organization
import play.api.db.DB
import anorm._
import anorm.SqlParser._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play.current

import scala.concurrent.Future

/**
  * Created by ryan on 3/9/16.
  */
class OrganizationDAOImpl @Inject()() extends OrganizationDAO {

  import OrganizationDAOImpl._

  override def find(id: Long): Future[Option[Organization]] = Future {
    DB.withConnection { implicit conn =>
      organizationSelectByIDSQL.on(
        'id -> id
      )
        .executeQuery()
        .as(organizationSelectParser.singleOpt)
    }
  }

  override def save(org: Organization): Future[Organization] = {
    org.id match {
      case None => insert(org)
      case Some(id) =>
        find(id) flatMap {
          case Some(_) => update(org)
          case None => Future.failed(new IllegalStateException(s"Unable to update Organization when unable to find ID ${id}"))
        }
    }
  }

  private def insert(org: Organization): Future[Organization] = Future {
    DB.withConnection { implicit conn =>
      organizationInsertSQL
        .on(
          'name -> org.name
        ).executeInsert(organizationIDParser.singleOpt)
    }
  } flatMap {
    case Some(id) =>
      find(id) map { _.get }
    case None => Future.failed(new IllegalStateException(s"Unable to insert Organization with name ${org.name}"))
  }

  private def update(org: Organization): Future[Organization] = {
    org.id match {
      case None => Future.failed(new IllegalStateException(s"Unable to update Organization without an ID"))
      case Some(id) => Future {
        DB.withConnection { implicit conn =>
          organizationUpdateSQL.on(
            'id -> id,
            'name -> org.name
          ).executeUpdate()
        }
      } flatMap { _ =>
        find(org.id.get)
      } map { _.get }
    }
  }
}

object OrganizationDAOImpl {
  val organizationInsertSQL = SQL(
    """
      |insert into organization (
      |  name
      |) values (
      |  {name}
      |)
    """.stripMargin
  )
  val organizationIDParser = long("id")

  val organizationUpdateSQL = SQL(
    """
      |update organization
      |set
      |name = {name}
      |where
      |id = {id}
    """.stripMargin
  )

  val organizationSelectByIDSQL = SQL(
    """
      |select id, name
      |from organization
      |where id = {id}
    """.stripMargin
  )

  val organizationSelectParser = for {
    id <- long("id")
    name <- str("name")
  } yield {
    Organization(Some(id), name)
  }
}