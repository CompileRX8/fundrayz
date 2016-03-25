package models.daos.organization

import java.util.UUID
import javax.inject.Inject

import anorm._
import anorm.SqlParser._
import models.daos.AbstractModelDAO
import models.daos.security.UserDAO
import models.{Contact, Organization}
import play.api.libs.concurrent.Execution.Implicits._

/**
  * Created by ryan on 3/16/16.
  */
class ContactDAOImpl @Inject()(userDAO: UserDAO) extends AbstractModelDAO[Contact, Organization] {

  private val selectString =
    """
      |select ui.id, c.person, c.org_id, o.name
      |from contact c
      |inner join organization o on c.org_id = o.id
      |inner join user_info ui on c.person = ui.user_id
    """.stripMargin

  override protected val selectSQL: SqlQuery = SQL(
    selectString +
    """
      |where ui.id = {id}
    """.stripMargin
  )

  override protected def getNamedParameters(t: Contact): Option[List[NamedParameter]] =
    for {
      org <- t.orgs.headOption
      orgId <- org.idOpt
    } yield {
      List[NamedParameter](
        'person -> t.person.userID,
        'org_id -> orgId
      )
    }

  override protected val parser: RowParser[Contact] =
    for {
      id <- long("ui.id")
      personId <- get[UUID]("c.person")
      orgId <- long("c.org_id")
      orgName <- str("o.name")
    } yield {
      Contact(
        Some(id),
        null,
        List(Organization(Some(orgId), orgName))
      )
    }

  override protected val insertSQL: SqlQuery = SQL(
    """
      |insert into contact (
      |  person,
      |  org_id
      |) values (
      |  {person},
      |  {org_id}
      |)
    """.stripMargin
  )
  override protected val selectAllSQL: SqlQuery = SQL(selectString)
  override protected val updateSQL: SqlQuery = SQL("")
  override protected val selectBySQL: SqlQuery = SQL(
    selectString +
    """
      |where c.org_id = {org_id}
    """.stripMargin
  )
}
