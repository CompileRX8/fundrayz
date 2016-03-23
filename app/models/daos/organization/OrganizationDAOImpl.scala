package models.daos.organization

import anorm.SqlParser._
import anorm._
import models.Organization
import models.daos.ModelDAO
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

/**
  * Created by ryan on 3/9/16.
  */
class OrganizationDAOImpl extends ModelDAO[Organization, Organization] {

  import OrganizationDAOImpl._

  override def find(id: Long): Future[Option[Organization]] =
    find(id, organizationSelectByIDSQL, organizationSelectParser)

  override def findBy(org: Organization): Future[Option[List[Organization]]] =
    org.id match {
      case Some(id) =>
        find(id) map {
          case Some(o) => Some(List(o))
          case None => None
        }
      case None => Future.failed(new IllegalStateException(s"Unable to find Organization by Organization without an ID"))
    }

  override def save(org: Organization): Future[Organization] =
    save(org, org.id, insert, update)

  override protected def insert(org: Organization): Future[Organization] =
    insert(organizationInsertSQL, organizationIDParser, 'name -> org.name)

  override protected def update(org: Organization): Future[Organization] =
    update(org.id, organizationUpdateSQL, 'name -> org.name)

  override def all: Future[List[Organization]] = all(organizationSelectAllSQL, organizationSelectParser)
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

  val organizationSelectAllSQL = SQL(
    """
      |select id, name
      |from organization
    """.stripMargin
  )

  val organizationSelectParser = for {
    id <- long("id")
    name <- str("name")
  } yield {
    Organization(Some(id), name)
  }
}