package models.daos.security

import anorm.SqlParser._
import anorm._
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import play.api.Play.current
import play.api.db.DB
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

/**
  * Data access for PasswordInfo objects.
  */
class PasswordInfoDAO extends DelegableAuthInfoDAO[PasswordInfo] {

  import PasswordInfoDAO._
  import UserDAOImpl.{loginInfoIDParser, loginInfoSelectIDByProviderIDAndProviderKeySQL}

  /**
    * Finds the auth info which is linked with the specified login info.
    *
    * @param loginInfo The linked login info.
    * @return The retrieved auth info or None if no auth info could be retrieved for the given login info.
    */
  def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = Future {
    DB.withConnection { implicit conn =>
      passwordInfoSelectByLoginInfoSQL
        .on(
          'provider_id -> loginInfo.providerID,
          'provider_key -> loginInfo.providerKey
        )
        .as(passwordInfoParser.singleOpt)
    }
  }

  /**
    * Adds new auth info for the given login info.
    *
    * @param loginInfo The login info for which the auth info should be added.
    * @param authInfo The auth info to add.
    * @return The added auth info.
    */
  def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = Future {
    DB.withConnection { implicit conn =>
      val loginInfoIDOpt = loginInfoSelectIDByProviderIDAndProviderKeySQL
        .on(
          'provider_id -> loginInfo.providerID,
          'provider_key -> loginInfo.providerKey
        )
        .as(loginInfoIDParser.singleOpt)
      loginInfoIDOpt match {
        case Some(loginInfoID) =>
          passwordInfoInsertSQL
            .on(
              'login_info_id -> loginInfoID,
              'hasher -> authInfo.hasher,
              'password -> authInfo.password,
              'salt -> authInfo.salt
            )
            .executeInsert()
        case None =>
          throw new IllegalStateException("LoginInfo could not be found to add PasswordInfo")
      }
    }
    authInfo
  }

  /**
    * Updates the auth info for the given login info.
    *
    * @param loginInfo The login info for which the auth info should be updated.
    * @param authInfo The auth info to update.
    * @return The updated auth info.
    */
  def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = Future {
    DB.withConnection { implicit conn =>
      val loginInfoIDOpt = loginInfoSelectIDByProviderIDAndProviderKeySQL
        .on(
          'provider_id -> loginInfo.providerID,
          'provider_key -> loginInfo.providerKey
        )
        .as(loginInfoIDParser.singleOpt)
      loginInfoIDOpt match {
        case Some(loginInfoID) =>
          passwordInfoUpdateSQL
            .on(
              'login_info_id -> loginInfoID,
              'hasher -> authInfo.hasher,
              'password -> authInfo.password,
              'salt -> authInfo.salt
            ).executeUpdate()
        case None =>
          throw new IllegalStateException("LoginInfo could not be found to update PasswordInfo")
      }
      authInfo
    }
  }

  /**
    * Saves the auth info for the given login info.
    *
    * This method either adds the auth info if it doesn't exists or it updates the auth info
    * if it already exists.
    *
    * @param loginInfo The login info for which the auth info should be saved.
    * @param authInfo The auth info to save.
    * @return The saved auth info.
    */
  def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    find(loginInfo).flatMap {
      case Some(_) => update(loginInfo, authInfo)
      case None => add(loginInfo, authInfo)
    }
  }

  /**
    * Removes the auth info for the given login info.
    *
    * @param loginInfo The login info for which the auth info should be removed.
    * @return A future to wait for the process to be completed.
    */
  def remove(loginInfo: LoginInfo): Future[Unit] = {
    find(loginInfo) map {
      case Some(passwordInfo) =>
        DB.withConnection { implicit conn =>
          val loginInfoIDOpt = loginInfoSelectIDByProviderIDAndProviderKeySQL
            .on(
              'provider_id -> loginInfo.providerID,
              'provider_key -> loginInfo.providerKey
            )
            .as(loginInfoIDParser.singleOpt)
          loginInfoIDOpt match {
            case Some(id) =>
              passwordInfoDeleteSQL
                .on(
                  'login_info_id -> id
                )
                .execute()
            case _ =>
          }
        }
      case None =>
    }
  }
}

object PasswordInfoDAO {

  val passwordInfoSelectByLoginInfoSQL = SQL(
    """
      |select *
      |from password_info pi
      |inner join login_info li on pi.login_info_id = li.id
      |where li.provider_id = {provider_id}
      |and li.provider_key = {provider_key}
    """.stripMargin)

  val passwordInfoInsertSQL = SQL(
    """
      |insert into password_info (
      |  login_info_id,
      |  hasher,
      |  password,
      |  salt
      |) values (
      |  {login_info_id},
      |  {hasher},
      |  {password},
      |  {salt}
      |)
    """.stripMargin)

  val passwordInfoUpdateSQL = SQL(
    """
      |update password_info
      |set
      |hasher = {hasher},
      |password = {password},
      |salt = {salt}
      |where
      |login_info_id = {login_info_id}
    """.stripMargin)

  val passwordInfoDeleteSQL = SQL(
    """
      |delete password_info
      |where
      |login_info_id = {login_info_id}
    """.stripMargin
  )

  val passwordInfoParser = for {
    hasher <- str("hasher")
    password <- str("password")
    salt <- str("salt").?
  } yield {
    PasswordInfo(hasher, password, salt)
  }
}
