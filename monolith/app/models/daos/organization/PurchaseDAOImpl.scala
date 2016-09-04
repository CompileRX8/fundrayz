package models.daos.organization

import javax.inject.Inject

import anorm._
import anorm.SqlParser._
import models.daos.AbstractModelDAO
import models._
import play.api.Play.current

import scala.concurrent.Future

/**
  * Created by ryan on 3/16/16.
  */
class PurchaseDAOImpl @Inject()() extends AbstractModelDAO[Purchase, Contact] with PurchaseDAO {
  override def findBy(org: Organization): Future[Option[List[Purchase]]] = ???

  override val selectAlias = "purchase"
  override val selectString =
    s"""
       |$selectAlias.id, $selectAlias.item_id, $selectAlias.purchaser, $selectAlias.cashier, $selectAlias.amount
       |from purchase $selectAlias
    """.stripMargin

  override protected val selectSQL: SqlQuery = SQL(
    s"""
       |select
       |$selectString
       |where $selectAlias.id = {id}
    """.stripMargin
  )

  override protected def getNamedParameters(t: Purchase): Option[List[NamedParameter]] =
    t.item.idOpt map { itemId =>
      List[NamedParameter](
        'item_id -> itemId,
        'purchaser -> t.purchaser.idToken,
        'cashier -> t.cashier.idToken,
        'amount -> t.amount
      )
    }

  override val parser: RowParser[Purchase] =
    for {
      id <- long("p.id")
      itemId <- long("p.item_id")
      purchaserId <- str("p.purchaser")
      cashierId <- str("p.cashier")
      amount <- double("amount")
    } yield {
      Purchase(
        Some(id),
        null,
        null,
        null,
        amount
      )
    }

  override protected val insertSQL: SqlQuery = SQL(
    s"""
       |insert into purchase (
       |  item_id,
       |  purchaser,
       |  cashier,
       |  amount
       |) values (
       |  {item_id},
       |  {purchaser},
       |  {cashier},
       |  {amount}
       |)
    """.stripMargin
  )
  override protected val selectAllSQL: SqlQuery = SQL(selectString)
  override protected val updateSQL: SqlQuery = SQL(
    s"""
       |update purchase
       |set
       |item_id = {item_id},
       |purchaser = {purchaser},
       |cashier = {cashier},
       |amount = {amount}
       |where id = {id}
    """.stripMargin
  )
  override protected val selectBySQL: SqlQuery = SQL(
    s"""
       |select
       |$selectString
       |where $selectAlias.purchaser = {id}
    """.stripMargin
  )
}
