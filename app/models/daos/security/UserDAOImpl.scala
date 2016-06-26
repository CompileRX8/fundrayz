package models.daos.security

import javax.inject.Inject

import anorm._
import anorm.SqlParser._
import models.User
import models.daos.AbstractModelDAO
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by ryan on 4/11/16.
  */
class UserDAOImpl @Inject()(implicit ec: ExecutionContext) extends AbstractModelDAO[User, User] with UserDAO {
  override val selectAlias: String = "usr"

  override val selectString: String =
    s"""
       |$selectAlias.id, $selectAlias.id_token, $selectAlias.first_name,
       |$selectAlias.last_name, |$selectAlias.email, $selectAlias.tags::jsonb
       |from user_info $selectAlias
     """.stripMargin

  override protected def getNamedParameters(t: User): Option[List[NamedParameter]] =
    Some(List[NamedParameter](
      'id_token -> t.idToken,
      'first_name -> t.firstName,
      'last_name -> t.lastName,
      'email -> t.email,
      'tags -> t.tags.map(Json.stringify)
    ))

  override val parser: RowParser[User] = for {
    id <- long(selectAlias + ".id")
    idToken <- str(selectAlias + ".id_token")
    firstName <- str(selectAlias + ".first_name").?
    lastName <- str(selectAlias + ".last_name").?
    email <- str(selectAlias + ".email").?
    tags <- str(selectAlias + ".tags").?
  } yield {
    val jsTags = tags.map(Json.parse)
    User(
      Some(id),
      idToken,
      firstName,
      lastName,
      email,
      jsTags
    )
  }

  override protected val insertSQL: SqlQuery = SQL(
    s"""
       |insert into user_info (
       |  id_token,
       |  first_name,
       |  last_name,
       |  email,
       |  tags
       |) values (
       |  {id_token},
       |  {first_name},
       |  {last_name},
       |  {email},
       |  {tags}::jsonb
       |)
     """.stripMargin
  )
  override protected val selectAllSQL: SqlQuery = SQL(s"select $selectString")
  override protected val updateSQL: SqlQuery = SQL(
    s"""
       |update user_info
       |set
       |id_token = {id_token},
       |first_name = {first_name},
       |last_name = {last_name},
       |email = {email},
       |tags = {tags}::jsonb
       |where id = {id}
     """.stripMargin
  )

  override protected val selectSQL: SqlQuery = SQL(
    s"""
       |select
       |$selectString
       |where id = {id}
     """.stripMargin
  )

  override protected val selectBySQL: SqlQuery = selectSQL

  private val selectByIDTokenSQL: SqlQuery = SQL(
    s"""
       |select
       |$selectString
       |where id_token = {id_token}
     """.stripMargin
  )
  override def findBy(idToken: String): Future[Option[User]] = {
    val namedParameters: List[NamedParameter] = List( 'id_token -> idToken )
    findByOther(namedParameters, selectByIDTokenSQL, parser) map { optUsers =>
      optUsers flatMap { _.headOption }
    }
  }
}
