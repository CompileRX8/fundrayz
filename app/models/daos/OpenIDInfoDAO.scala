package models.daos

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.impl.providers.OpenIDInfo
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play.current

import anorm._
import anorm.SqlParser._
import play.api.db.DB

import scala.concurrent.Future

/**
  * Data access for OpenIDInfo objects and their attribute sets.
  */
class OpenIDInfoDAO extends DelegableAuthInfoDAO[OpenIDInfo] {

  import OpenIDInfoDAO._
  import UserDAOImpl.{loginInfoSelectIDByProviderIDAndProviderKeySQL, loginInfoIDParser}

  /**
    * Finds the auth info which is linked with the specified login info.
    *
    * @param loginInfo The linked login info.
    * @return The retrieved auth info or None if no auth info could be retrieved for the given login info.
    */
  def find(loginInfo: LoginInfo): Future[Option[OpenIDInfo]] = Future {
    DB.withConnection { implicit conn =>
      val openIDInfoOpt = openIDInfoSelectByLoginInfoSQL
        .on(
          'provider_id -> loginInfo.providerID,
          'provider_key -> loginInfo.providerKey
        )
        .as(openIDInfoParser.singleOpt)
      openIDInfoOpt map {
        case (id, partialOpenID) =>
          val attrsMap = openIDInfoAttrsSelectByOpenIDInfoIDSQL
            .on('openid_info_id -> id)
            .as(openIDInfoAttrsParser.map(flatten).*).toMap
          partialOpenID.copy(attributes = attrsMap)
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
  def add(loginInfo: LoginInfo, authInfo: OpenIDInfo): Future[OpenIDInfo] = Future {
    DB.withConnection { implicit conn =>
      val loginInfoIDOpt = loginInfoSelectIDByProviderIDAndProviderKeySQL
        .on(
          'provider_id -> loginInfo.providerID,
          'provider_key -> loginInfo.providerKey
        )
        .as(loginInfoIDParser.singleOpt)
      loginInfoIDOpt match {
        case Some(loginInfoID) =>
          val openIDInfoIDOpt = openIDInfoInsertSQL
            .on(
              'login_info_id -> loginInfoID,
              'open_id -> authInfo.id
            )
            .executeInsert(openIDInfoIDParser.singleOpt)
          openIDInfoIDOpt match {
            case Some(openIDInfoID) =>
              authInfo.attributes foreach {
                case (name, value) =>
                  openIDInfoAttrsInsertSQL
                    .on(
                      'openid_info_id -> openIDInfoID,
                      'attribute_name -> name,
                      'attribute_value -> value
                    )
              }
              authInfo
            case None => throw new IllegalStateException("Unable to add OpenIDInfo")
          }
        case None => throw new IllegalStateException("Unable to find LoginInfo to add OpenIDInfo")
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
  def update(loginInfo: LoginInfo, authInfo: OpenIDInfo): Future[OpenIDInfo] = {
    remove(loginInfo) flatMap { _ =>
      add(loginInfo, authInfo)
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
  def save(loginInfo: LoginInfo, authInfo: OpenIDInfo): Future[OpenIDInfo] = {
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
      openIDInfoSelectByLoginInfoSQL
        .on(
          'provider_id -> loginInfo.providerID,
          'provider_key -> loginInfo.providerKey
        )
        .as(openIDInfoIDParser.singleOpt)
        .foreach { openIDInfoID =>
          openIDInfoAttrsDeleteByOpenIDInfoIDSQL.on('openid_info_id -> openIDInfoID).execute()
          openIDInfoDeleteByOpenIDInfoIDSQL.on('openid_info_id -> openIDInfoID).execute()
        }
    }
  }
}

object OpenIDInfoDAO {

  val openIDInfoSelectByLoginInfoSQL = SQL(
    """
      |select *
      |from openid_info oi
      |inner join login_info li on oi.login_info_id = li.id
      |where li.provider_id = {provider_id}
      |and li.provider_key = {provider_key}
    """.stripMargin)

  val openIDInfoIDParser = long("openid_info.id")
  val openIDInfoParser = for {
    id <- long("openid_info.id")
    openID <- str("open_id")
  } yield (id, OpenIDInfo(openID, Map.empty))

  val openIDInfoAttrsSelectByOpenIDInfoIDSQL = SQL(
    """
      |select *
      |from openid_info_attributes
      |where openid_info_id = {openid_info_id}
    """.stripMargin)

  val openIDInfoAttrsParser = str("attribute_name") ~ str("attribute_value")

  val openIDInfoInsertSQL = SQL(
    """
      |insert into openid_info (
      |  login_info_id,
      |  open_id
      |) values (
      |  {login_info_id},
      |  {open_id}
      |)
    """.stripMargin)

  val openIDInfoAttrsInsertSQL = SQL(
    """
      |insert into openid_info_attributes (
      |  openid_info_id,
      |  attribute_name,
      |  attribute_value
      |) values (
      |  {openid_info_id},
      |  {attribute_name},
      |  {attribute_value}
      |)
    """.stripMargin)

  val openIDInfoDeleteByOpenIDInfoIDSQL = SQL(
    """
      |delete
      |from openid_info
      |where id = {openid_info_id}
    """.stripMargin)

  val openIDInfoAttrsDeleteByOpenIDInfoIDSQL = SQL(
    """
      |delete
      |from openid_info_attributes
      |where openid_info_id = {openid_info_id}
    """.stripMargin)
}
