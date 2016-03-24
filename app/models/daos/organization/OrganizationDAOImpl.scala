package models.daos.organization

import anorm.SqlParser._
import anorm.{NamedParameter, _}
import models.Organization
import models.daos.AbstractModelDAO
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

/**
  * Created by ryan on 3/9/16.
  */
class OrganizationDAOImpl extends AbstractModelDAO[Organization, Organization] {

  override def find(id: Long): Future[Option[Organization]] =
    find(id, selectSQL, parser)

  override def findBy(org: Organization): Future[Option[List[Organization]]] =
    org.id match {
      case Some(id) =>
        find(id) map {
          case Some(o) => Some(List(o))
          case None => None
        }
      case None => Future.failed(new IllegalStateException(s"Unable to find Organization by Organization without an ID"))
    }

  override protected def insert(org: Organization): Future[Organization] =
    insertWithParams(org)

  override protected def update(org: Organization): Future[Organization] =
    updateWithParams(org)

  override protected def getNamedParameters(org: Organization): Option[List[NamedParameter]] = {
    Some(List[NamedParameter](
      'name -> org.name
    ))
  }

  override protected val insertSQL = SQL(
    """
      |insert into organization (
      |  name
      |) values (
      |  {name}
      |)
    """.stripMargin
  )

  override protected val updateSQL = SQL(
    """
      |update organization
      |set
      |name = {name}
      |where
      |id = {id}
    """.stripMargin
  )

  override protected val selectSQL = SQL(
    """
      |select id, name
      |from organization
      |where id = {id}
    """.stripMargin
  )
  override protected val selectBySQL = selectSQL

  override protected val selectAllSQL = SQL(
    """
      |select id, name
      |from organization
    """.stripMargin
  )

  override protected val parser = for {
    id <- long("id")
    name <- str("name")
  } yield {
    Organization(Some(id), name)
  }
}