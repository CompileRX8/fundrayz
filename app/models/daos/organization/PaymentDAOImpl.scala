package models.daos.organization

import java.util.UUID
import javax.inject.Inject

import anorm._
import anorm.SqlParser._
import models.daos.AbstractModelDAO
import models.daos.security.UserDAO
import models.{Contact, Organization, Payment}
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

/**
  * Created by ryan on 3/16/16.
  */
class PaymentDAOImpl @Inject()(userDAO: UserDAO) extends AbstractModelDAO[Payment, Contact] with PaymentDAO {
  override def findBy(org: Organization): Future[Option[List[Payment]]] = ???

  private val selectString =
    """
      |select id, payer, cashier, amount, description
      |from payment
    """.stripMargin

  override protected val selectSQL: SqlQuery = SQL(
    selectString +
      """
        |where id = {id}
      """.stripMargin
  )

  override protected def getNamedParameters(t: Payment): Option[List[NamedParameter]] =
    Some(List[NamedParameter](
      'payer -> t.payer.userID,
      'cashier -> t.cashier.userID,
      'amount -> t.amount,
      'description -> t.description
    ))

  override protected val parser: RowParser[Payment] =
    for {
      id <- long("id")
      payerId <- get[UUID]("payer")
      payerOpt <- userDAO.find(payerId)
      payer <- payerOpt
      cashierId <- get[UUID]("cashier")
      cashierOpt <- userDAO.find(cashierId)
      cashier <- cashierOpt
      amount <- double("amount")
      description <- str("description")
    } yield {
      Payment(
        Some(id),
        payer,
        cashier,
        amount,
        description
      )
    }
  override protected val insertSQL: SqlQuery = SQL(
    """
      |insert into payment (
      |  payer,
      |  cashier,
      |  amount,
      |  description
      |) values (
      |  {payer}::uuid,
      |  {cashier}::uuid,
      |  {amount},
      |  {description}
      |)
    """.stripMargin
  )
  override protected val selectAllSQL: SqlQuery = SQL(selectString)
  override protected val updateSQL: SqlQuery = SQL(
    """
      |update payment
      |set
      |payer = {payer}::uuid,
      |cashier = {cashier}::uuid,
      |amount = {amount},
      |description = {description}
      |where id = {id}
    """.stripMargin
  )
  override protected val selectBySQL: SqlQuery = SQL(
    selectString +
      """
        |where payer = {payer}::uuid
      """.stripMargin
  )
  private val selectByPayerSQL: SqlQuery = SQL(
    selectString +
      """
        |where payer = {payer}::uuid
      """.stripMargin
  )
}
