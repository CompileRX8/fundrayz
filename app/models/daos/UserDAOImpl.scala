package models.daos

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import models.User

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play.current

import anorm._
import anorm.SqlParser._
import play.api.db.DB

/**
  * Data access for User objects.
  */
class UserDAOImpl extends UserDAO {

  import UserDAOImpl._

  /**
    * Finds a user by its login info.
    *
    * @param loginInfo The login info of the user to find.
    * @return The found user or None if no user for the given login info could be found.
    */
  def find(loginInfo: LoginInfo): Future[Option[User]] = Future {
    DB.withConnection { implicit conn =>
      userInfoSelectByLoginInfoSQL
        .on(
          'provider_id -> loginInfo.providerID,
          'provider_key -> loginInfo.providerKey
        )
        .executeQuery()
        .as(userInfoSelectParser.singleOpt)
    }
  }

  /**
    * Finds a user by its user ID.
    *
    * @param userID The ID of the user to find.
    * @return The found user or None if no user for the given ID could be found.
    */
  def find(userID: UUID): Future[Option[User]] = Future {
    DB.withConnection { implicit conn =>
      userInfoSelectByUUIDSQL
        .on('user_id -> userID)
        .executeQuery()
        .as(userInfoSelectParser.singleOpt)
    }
  }

  /**
    * Saves a user.
    *
    * @param user The user to save.
    * @return The saved user.
    */
  def save(user: User): Future[User] = {
    // If the user's ID already exists, update the User fields.
    // Otherwise, insert the new User
    find(user.userID) flatMap {
      case Some(_) => update(user)
      case None => insert(user)
    }
  }

  private def insert(user: User): Future[User] = Future {
    DB.withConnection { implicit conn =>
      loginInfoInsertSQL
        .on(
          'provider_id -> user.loginInfo.providerID,
          'provider_key -> user.loginInfo.providerKey
        ).executeInsert(loginInfoIDParser.singleOpt)
      match {
        case Some(id) =>
          userInfoInsertSQL.on(
            'user_id -> user.userID,
            'login_info_id -> id,
            'first_name -> user.firstName,
            'last_name -> user.lastName,
            'full_name -> user.fullName,
            'email -> user.email,
            'avatar_url -> user.avatarURL
          ).executeInsert(userInfoInsertParser.single)
            .copy(loginInfo = user.loginInfo)
        case None => throw new IllegalStateException("Unable to add LoginInfo when adding User")
      }
    }
  }

  private def update(user: User): Future[User] = Future {
    DB.withConnection { implicit conn =>
      userInfoUpdateSQL.on(
        'user_id -> user.userID,
        'first_name -> user.firstName,
        'last_name -> user.lastName,
        'full_name -> user.fullName,
        'email -> user.email,
        'avatar_url -> user.avatarURL
      ).executeUpdate()
    }
  } flatMap { _ =>
    find(user.userID)
  } map { _.get }
}

object UserDAOImpl {

  val loginInfoInsertSQL = SQL(
    """
      |insert into login_info (
      |  provider_id,
      |  provider_key
      |) values (
      |  {provider_id},
      |  {provider_key}
      |)
    """.stripMargin)

  val loginInfoSelectIDByProviderIDAndProviderKeySQL = SQL(
    """
      |select id
      |from login_info
      |where provider_id = {provider_id}
      |and provider_key = {provider_key}
    """.stripMargin)

  val loginInfoIDParser = long("id")

  val userInfoSelectByLoginInfoSQL = SQL(
    """
      |select *
      |from user_info ui
      |inner join login_info li on ui.login_info_id = li.id
      |where li.provider_id = {provider_id}
      |and li.provider_key = {provider_key}
    """.stripMargin)

  val userInfoSelectByUUIDSQL = SQL(
    """
      |select *
      |from user_info ui
      |inner join login_info li on ui.login_info_id = li.id
      |where ui.user_id = {user_id}::uuid
    """.stripMargin)

  val userInfoInsertSQL = SQL(
    """
      |insert into user_info (
      |  user_id,
      |  login_info_id,
      |  first_name,
      |  last_name,
      |  full_name,
      |  email,
      |  avatar_url
      |) values (
      |  {user_id}::uuid,
      |  {login_info_id},
      |  {first_name},
      |  {last_name},
      |  {full_name},
      |  {email},
      |  {avatar_url}
      |)
    """.stripMargin)

  val userInfoUpdateSQL = SQL(
    """
      |update user_info
      |set
      |first_name = {first_name},
      |last_name = {last_name},
      |full_name = {full_name},
      |email = {email},
      |avatar_url = {avatar_url}
      |where
      |user_id = {user_id}::uuid
    """.stripMargin)

  val userInfoInsertParser = for {
    user_id <- get[UUID]("user_id")
    first_name <- str("first_name").?
    last_name <- str("last_name").?
    full_name <- str("full_name").?
    email <- str("email").?
    avatar_url <- str("avatar_url").?
  } yield {
    User(
      user_id,
      null,
      first_name,
      last_name,
      full_name,
      email,
      avatar_url
    )
  }

  val userInfoSelectParser = for {
    partialUser <- userInfoInsertParser
    provider_id <- str("provider_id")
    provider_key <- str("provider_key")
  } yield {
    partialUser.copy(loginInfo = LoginInfo(
      provider_id,
      provider_key
    )
    )
  }
}
