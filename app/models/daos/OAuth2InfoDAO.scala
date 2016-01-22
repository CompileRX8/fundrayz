package models.daos

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.impl.providers.OAuth2Info
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play.current

import anorm._
import anorm.SqlParser._
import play.api.db.DB

import scala.concurrent.Future

/**
  * Data access for OAuth2Info objects.
  */
class OAuth2InfoDAO extends DelegableAuthInfoDAO[OAuth2Info] {

  import OAuth2InfoDAO._
  import UserDAOImpl.{loginInfoSelectIDByProviderIDAndProviderKeySQL, loginInfoIDParser}

  /**
    * Finds the auth info which is linked with the specified login info.
    *
    * @param loginInfo The linked login info.
    * @return The retrieved auth info or None if no auth info could be retrieved for the given login info.
    */
  def find(loginInfo: LoginInfo): Future[Option[OAuth2Info]] = Future {
    DB.withConnection { implicit conn =>
      oauth2InfoSelectByLoginInfoSQL
        .on(
          'provider_id -> loginInfo.providerID,
          'provider_key -> loginInfo.providerKey
        )
        .as(oauth2InfoParser.singleOpt)
        .map {
          case (id, partialOAuth2Info) =>
            val paramsMap = oauth2InfoParamsSelectByOAuth2InfoIDSQL
              .on('oauth2_info_id -> id)
              .as(oauth2InfoParamsParser.map(flatten).*).toMap
            if (paramsMap.isEmpty)
              partialOAuth2Info
            else
              partialOAuth2Info.copy(params = Some(paramsMap))
        }
    }
  }

  /**
    * Adds new auth info for the given login info.
    *
    * @param loginInfo The login info for which the auth info should be added.
    * @param authInfo The auth info to add.
    * @return The added auth info.
    */
  def add(loginInfo: LoginInfo, authInfo: OAuth2Info): Future[OAuth2Info] = Future {
    DB.withConnection { implicit conn =>
      loginInfoSelectIDByProviderIDAndProviderKeySQL
        .on(
          'provider_id -> loginInfo.providerID,
          'provider_key -> loginInfo.providerKey
        )
        .as(loginInfoIDParser.singleOpt)
      match {
        case Some(loginInfoID) =>
          val oauth2InfoIDOpt = oauth2InfoInsertSQL
            .on(
              'login_info_id -> loginInfoID,
              'access_token -> authInfo.accessToken,
              'token_type -> authInfo.tokenType,
              'expires_in -> authInfo.expiresIn,
              'refresh_token -> authInfo.refreshToken
            )
            .executeInsert(oauth2InfoIDParser.singleOpt)
          (oauth2InfoIDOpt, authInfo.params) match {
            case (Some(oauth2InfoID), Some(paramsMap: Map[String, String])) =>
              paramsMap foreach {
                case (name, value) =>
                  oauth2InfoParamsInsertSQL
                    .on(
                      'oauth2_info_id -> oauth2InfoID,
                      'param_name -> name,
                      'param_value -> value
                    )
                    .executeInsert()
              }
            case (None, _) => throw new IllegalStateException("Unable to add OAuth2Info")
            case (_, _) =>
          }
          authInfo
        case None => throw new IllegalStateException("Unable to find LoginInfo to add OAuth2Info")
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
  def update(loginInfo: LoginInfo, authInfo: OAuth2Info): Future[OAuth2Info] = {
    remove(loginInfo) flatMap { _ => add(loginInfo, authInfo) }
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
  def save(loginInfo: LoginInfo, authInfo: OAuth2Info): Future[OAuth2Info] = {
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
      oauth2InfoSelectByLoginInfoSQL
        .on(
          'provider_id -> loginInfo.providerID,
          'provider_key -> loginInfo.providerKey
        )
        .as(oauth2InfoIDParser.singleOpt)
        .foreach { oauth2InfoID =>
          oauth2InfoParamsDeleteByOAuth2InfoIDSQL.on('oauth2_info_id -> oauth2InfoID).execute()
          oauth2InfoDeleteByOAuth2InfoIDSQL.on('oauth2_info_id -> oauth2InfoID).execute()
        }
    }
  }
}

object OAuth2InfoDAO {

  val oauth2InfoSelectByLoginInfoSQL = SQL(
    """
      |select *
      |from oauth2_info oi
      |inner join login_info li on oi.login_info_id = li.id
      |where li.provider_id = {provider_id}
      |and li.provider_key = {provider_key}
    """.stripMargin)

  val oauth2InfoIDParser = long("oauth2_info.id")
  val oauth2InfoParser = for {
    id <- long("oauth2_info.id")
    accessToken <- str("access_token")
    tokenType <- str("token_type").?
    expiresIn <- int("expires_in").?
    refreshToken <- str("refresh_token").?
  } yield (id, OAuth2Info(accessToken, tokenType, expiresIn, refreshToken, None))

  val oauth2InfoParamsSelectByOAuth2InfoIDSQL = SQL(
    """
      |select *
      |from oauth2_info_params
      |where oauth2_info_id = {oauth2_info_id}
    """.stripMargin)

  val oauth2InfoParamsParser = str("param_name") ~ str("param_value")

  val oauth2InfoInsertSQL = SQL(
    """
      |insert into oauth2_info (
      |  login_info_id,
      |  access_token,
      |  token_type,
      |  expires_in,
      |  refresh_token
      |) values (
      |  {login_info_id},
      |  {access_token},
      |  {token_type},
      |  {expires_in},
      |  {refresh_token}
      |)
    """.stripMargin)

  val oauth2InfoParamsInsertSQL = SQL(
    """
      |insert into oauth2_info_params (
      |  oauth2_info_id,
      |  param_name,
      |  param_value
      |) values (
      |  {oauth2_info_id},
      |  {param_name},
      |  {param_value}
      |)
    """.stripMargin)

  val oauth2InfoDeleteByOAuth2InfoIDSQL = SQL(
    """
      |delete
      |from oauth2_info
      |where id = {oauth2_info_id}
    """.stripMargin)

  val oauth2InfoParamsDeleteByOAuth2InfoIDSQL = SQL(
    """
      |delete
      |from oauth2_info_params
      |where oauth2_info_id = {oauth2_info_id}
    """.stripMargin)
}
