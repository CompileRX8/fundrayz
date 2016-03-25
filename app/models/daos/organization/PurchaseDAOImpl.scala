package models.daos.organization

import java.util.UUID
import javax.inject.Inject

import anorm._
import anorm.SqlParser._
import models.daos.AbstractModelDAO
import models.daos.security.UserDAO
import models.{Campaign, Contact, Organization, Purchase}
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

/**
  * Created by ryan on 3/16/16.
  */
class PurchaseDAOImpl @Inject()(userDAO: UserDAO) extends AbstractModelDAO[Purchase, Contact] with PurchaseDAO {
  override def findBy(org: Organization): Future[Option[List[Purchase]]] = ???

  override def findBy(campaign: Campaign): Future[Option[List[Purchase]]] = ???

  private val selectString =
    """
      |select p.id, p.item_id, p.purchaser, p.cashier, p.amount
      |from purchase p
    """.stripMargin

  override protected val selectSQL: SqlQuery = SQL(
    selectString +
    """
      |where p.id = {id}
    """.stripMargin
  )

  override protected def getNamedParameters(t: Purchase): Option[List[NamedParameter]] =
    t.item.idOpt map { itemId =>
      List[NamedParameter](
        'item_id -> itemId,
        'purchaser -> t.purchaser.userID,
        'cashier -> t.cashier.userID,
        'amount -> t.amount
      )
    }

  override protected val parser: RowParser[Purchase] =
    for {
      id <- long("p.id")
      itemId <- long("p.item_id")
      purchaserId <- get[UUID]("p.purchaser")
      purchaserOpt <- userDAO.find(purchaserId)
      purchaser <- purchaserOpt
      cashierId <- get[UUID]("p.cashier")
      cashierOpt <- userDAO.find(cashierId)
      cashier <- cashierOpt
      amount <- double("amount")
    } yield {
      Purchase(
        Some(id),
        null,
        purchaser,
        cashier,
        amount
      )
    }

  override protected val insertSQL: SqlQuery = SQL(
    """
      |insert into purchase (
      |  item_id,
      |  purchaser,
      |  cashier,
      |  amount
      |) values (
      |  {item_id},
      |  {purchaser}::uuid,
      |  {cashier}::uuid,
      |  {amount}
      |)
    """.stripMargin
  )
  override protected val selectAllSQL: SqlQuery = SQL(selectString)
  override protected val updateSQL: SqlQuery = SQL(
    """
      |update purchase
      |set
      |item_id = {item_id},
      |purchaser = {purchaser}::uuid,
      |cashier = {cashier}::uuid,
      |amount = {amount}
      |where id = {id}
    """.stripMargin
  )
  override protected val selectBySQL: SqlQuery = SQL(
    selectString +
    """
      |inner join user_info ui on p.purchaser = ui.uuid
      |where ui.id = {id}
    """.stripMargin
  )
}
