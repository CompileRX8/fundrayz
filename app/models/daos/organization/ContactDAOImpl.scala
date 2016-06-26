package models.daos.organization

import javax.inject.Inject

import anorm.SqlParser._
import anorm._
import models.daos.AbstractModelDAO
import models._
import models.daos.security.{UserDAO, UserRoleDAO}
import play.api.db.DB
import play.api.Play.current

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by ryan on 3/16/16.
  */
class ContactDAOImpl @Inject()(organizationDAO: AbstractModelDAO[Organization, Organization],
                               userDAO: UserDAO,
                               userRoleDAO: UserRoleDAO
                              )(implicit ec: ExecutionContext) extends AbstractModelDAO[Contact, Organization] {

  private case class ContactRow(idOpt: Option[Long], person: User, org: Organization, role: UserRole) extends WithID

  private object ContactRowDAO extends AbstractModelDAO[ContactRow, Organization] {
    override val selectAlias = "contact"
    override val selectString =
      s"""
         |$selectAlias.person,
         |${organizationDAO.selectAlias}.id, ${organizationDAO.selectAlias}.name,
         |${userDAO.selectString}
         |inner join organization $selectAlias on $selectAlias.org_id = ${organizationDAO.selectAlias}.id
         |inner join contact $selectAlias on $selectAlias.person = ${userDAO.selectAlias}.id
    """.stripMargin

    override protected val selectSQL: SqlQuery =
      SQL(
        s"""
           |select
           |$selectString
           |where ${userDAO.selectAlias}.id = {id}
    """.stripMargin
      )

    override protected def getNamedParameters(t: ContactRow): Option[List[NamedParameter]] =
      t.org.idOpt flatMap { orgId =>
        t.person.idOpt map { personId =>
          List[NamedParameter](
            'person -> personId,
            'org_id -> orgId,
            'user_role -> t.role.id
          )
        }
      }

    override val parser: RowParser[ContactRow] =
      (for {
        id <- long(userDAO.selectAlias + ".id")
        userRoleId <- long(selectAlias + ".user_role")
        personId <- str(selectAlias + ".person")
      } yield {
        ContactRow(
          Some(id),
          null,
          null,
          null
        )
      }) ~ organizationDAO.parser ~ userDAO.parser map {
        case contact ~ org ~ person =>
          contact.copy(person = person, org = org)
      }

    override protected val insertSQL: SqlQuery = SQL(
      s"""
         |insert into contact (
         |  person,
         |  org_id,
         |  user_role
         |) values (
         |  {person},
         |  {org_id},
         |  {user_role}
         |)
      """.stripMargin
    )
    override protected val selectAllSQL: SqlQuery = SQL(s"select $selectString")
    override protected val updateSQL: SqlQuery = SQL("")
    override protected val selectBySQL: SqlQuery = SQL(
      s"""
         |select
         |$selectString
         |where $selectAlias.org_id = {id}
      """.stripMargin
    )

    def delete(personID: String): Future[Int] = Future {
      DB.withConnection { implicit conn =>
        SQL("delete from contact where person = {person}").on('person -> personID).executeUpdate()
      }
    }
  }

  override def insert(contact: Contact): Future[Contact] = ???

  /* for {
     contactId <- contact.idOpt
     rowsDeleted <- ContactRowDAO.delete(contact.person.idToken.get)
     org <- contact.orgs
     contactRow <- ContactRowDAO.insert(ContactRow(None, org, contact.person.idToken.get))
   } yield {
     contact
   } */

  override def update(contact: Contact): Future[Contact] = insert(contact)

  override val selectAlias: String = ""

  override protected def getNamedParameters(t: Contact): Option[List[NamedParameter]] = None

  override val parser: RowParser[Contact] = null

  override protected val insertSQL: SqlQuery = SQL("")
  override protected val selectAllSQL: SqlQuery = SQL("")
  override protected val updateSQL: SqlQuery = SQL("")
  override val selectString: String = ""
  override protected val selectBySQL: SqlQuery = SQL("")
  override protected val selectSQL: SqlQuery = SQL("")
}
