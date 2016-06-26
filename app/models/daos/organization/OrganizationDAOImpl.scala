package models.daos.organization

import anorm.SqlParser._
import anorm._
import models.Organization
import models.daos.AbstractModelDAO
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

/**
  * Created by ryan on 3/9/16.
  */
class OrganizationDAOImpl extends AbstractModelDAO[Organization, Organization] {

  override def findBy(org: Organization): Future[Option[List[Organization]]] =
    org.idOpt match {
      case Some(id) =>
        find(id) map {
          case Some(o) => Some(List(o))
          case None => None
        }
      case None => Future.failed(new IllegalStateException(s"Unable to find Organization by Organization without an ID"))
    }

  override protected def getNamedParameters(org: Organization): Option[List[NamedParameter]] = {
    Some(List[NamedParameter](
      'name -> org.name
    ))
  }

  override protected val insertSQL = SQL(
    s"""
      |insert into organization (
      |  name
      |) values (
      |  {name}
      |)
    """.stripMargin
  )

  override protected val updateSQL = SQL(
    s"""
      |update organization
      |set
      |name = {name}
      |where
      |id = {id}
    """.stripMargin
  )

  override val selectAlias = "org"
  override val selectString =
    s"""
      |$selectAlias.id, $selectAlias.name
      |from organization $selectAlias
    """.stripMargin

  override protected val selectSQL = SQL(
    s"""
      |select
      |$selectString
      |where $selectAlias.id = {id}
    """.stripMargin
  )
  override protected val selectBySQL = selectSQL

  override protected val selectAllSQL = SQL(s"select $selectString")

  override val parser = for {
    id <- long(selectAlias + ".id")
    name <- str(selectAlias + ".name")
  } yield {
    Organization(Some(id), name)
  }
}