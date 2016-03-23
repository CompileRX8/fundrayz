package models.daos.security

import anorm.SqlParser._
import anorm._
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.impl.providers.OAuth1Info
import play.api.Play.current
import play.api.db.DB
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

/**
  * Data access for OAuth1Info objects.
  */
class OAuth1InfoDAO extends DelegableAuthInfoDAO[OAuth1Info] {

  import OAuth1InfoDAO._
  import UserDAOImpl.{loginInfoIDParser, loginInfoSelectIDByProviderIDAndProviderKeySQL}

  /**
    * Finds the auth info which is linked with the specified login info.
    *
    * @param loginInfo The linked login info.
    * @return The retrieved auth info or None if no auth info could be retrieved for the given login info.
    */
  def find(loginInfo: LoginInfo): Future[Option[OAuth1Info]] = Future {
    DB.withConnection { implicit conn =>
      oauth1InfoSelectByLoginInfoSQL
        .on(
          'provider_id -> loginInfo.providerID,
          'provider_key -> loginInfo.providerKey
        )
        .as(oauth1InfoParser.singleOpt)
    }
  }

  /**
    * Adds new auth info for the given login info.
    *
    * @param loginInfo The login info for which the auth info should be added.
    * @param authInfo The auth info to add.
    * @return The added auth info.
    */
  def add(loginInfo: LoginInfo, authInfo: OAuth1Info): Future[OAuth1Info] = Future {
    DB.withConnection { implicit conn =>
      loginInfoSelectIDByProviderIDAndProviderKeySQL
        .on(
          'provider_id -> loginInfo.providerID,
          'provider_key -> loginInfo.providerKey
        )
        .as(loginInfoIDParser.singleOpt)
      match {
        case Some(loginInfoID) =>
          oauth1InfoInsertSQL
            .on(
              'login_info_id -> loginInfoID,
              'token -> authInfo.token,
              'secret -> authInfo.secret
            )
            .executeInsert()
          authInfo
        case None => throw new IllegalStateException("Unable to find LoginInfo to add OAuth1Info")
      }
    }
  }

  /**
    * Updates the auth info for the given login info.
    *
    * @param loginInfo The login info for which the auth info should be updated.
    * @param authInfo The auth info to update.
    * @return The updated auth info.
    */
  def update(loginInfo: LoginInfo, authInfo: OAuth1Info): Future[OAuth1Info] = Future {
    DB.withConnection { implicit conn =>
      oauth1InfoSelectByLoginInfoSQL
        .on(
          'provider_id -> loginInfo.providerID,
          'provider_key -> loginInfo.providerKey
        )
        .as(oauth1InfoIDParser.singleOpt)
      match {
        case Some(oauth1InfoID) =>
          oauth1InfoUpdateByOAuth1InfoIDSQL
            .on(
              'oauth1_info_id -> oauth1InfoID,
              'token -> authInfo.token,
              'secret -> authInfo.secret
            )
          authInfo
        case None => throw new IllegalStateException("Unable to find LoginInfo to update OAuth1Info")
      }
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
  def save(loginInfo: LoginInfo, authInfo: OAuth1Info): Future[OAuth1Info] = {
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
  def remove(loginInfo: LoginInfo): Future[Unit] = Future {
    DB.withConnection { implicit conn =>
      oauth1InfoSelectByLoginInfoSQL
        .on(
          'provider_id -> loginInfo.providerID,
          'provider_key -> loginInfo.providerKey
        )
        .as(oauth1InfoIDParser.singleOpt)
      match {
        case Some(oauth1InfoID) =>
          oauth1InfoDeleteByOAuth1InfoIDSQL
            .on(
              'oauth1_info_id -> oauth1InfoID
            )
            .execute()
        case None => throw new IllegalStateException("Unable to find LoginInfo to delete OAuth1Info")
      }
    }
  }
}

object OAuth1InfoDAO {

  val oauth1InfoSelectByLoginInfoSQL = SQL(
    """
      |select *
      |from oauth1_info oi
      |inner join login_info li on oi.login_info_id = li.id
      |where li.provider_id = {provider_id}
      |and li.provider_key = {provider_key}
    """.stripMargin)

  val oauth1InfoIDParser = long("oauth1_info.id")
  val oauth1InfoParser = for {
    token <- str("token")
    secret <- str("secret")
  } yield OAuth1Info(token, secret)

  val oauth1InfoInsertSQL = SQL(
    """
      |insert into oauth1_info (
      |  login_info_id,
      |  token,
      |  secret
      |) values (
      |  {login_info_id},
      |  {token},
      |  {secret}
      |)
    """.stripMargin)

  val oauth1InfoUpdateByOAuth1InfoIDSQL = SQL(
    """
      |update oauth1_info
      |set
      |token = {token},
      |secret = {secret}
      |where
      |id = {oauth1_info_id}
    """.stripMargin)

  val oauth1InfoDeleteByOAuth1InfoIDSQL = SQL(
    """
      |delete
      |from oauth1_info
      |where
      |id = {oauth1_info_id}
    """.stripMargin)
}
