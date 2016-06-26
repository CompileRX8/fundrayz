package models.daos.organization

import javax.inject.Inject

import anorm._
import anorm.SqlParser._
import models.daos.AbstractModelDAO
import models.{Contact, Organization, Payment}
import play.api.Play.current

import scala.concurrent.Future

/**
  * Created by ryan on 3/16/16.
  */
class PaymentDAOImpl @Inject()() extends AbstractModelDAO[Payment, Contact] with PaymentDAO {
  override def findBy(org: Organization): Future[Option[List[Payment]]] = ???

  override val selectAlias = "pmt"
  override val selectString =
    s"""
       |$selectAlias.id, $selectAlias.payer, $selectAlias.cashier, $selectAlias.amount, $selectAlias.description
       |from payment $selectAlias
    """.stripMargin

  override protected val selectSQL: SqlQuery = SQL(
    s"""
       |select
       |$selectString
       |where $selectAlias.id = {id}
      """.stripMargin
  )

  override protected def getNamedParameters(t: Payment): Option[List[NamedParameter]] =
    Some(List[NamedParameter](
      'payer -> t.payer.idToken,
      'cashier -> t.cashier.idToken,
      'amount -> t.amount,
      'description -> t.description
    ))

  override val parser: RowParser[Payment] =
    for {
      id <- long("id")
      payerId <- str("payer")
      cashierId <- str("cashier")
      amount <- double("amount")
      description <- str("description")
    } yield {
      Payment(
        Some(id),
        null,
        null,
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
      |  {payer},
      |  {cashier},
      |  {amount},
      |  {description}
      |)
    """.stripMargin
  )
  override protected val selectAllSQL: SqlQuery = SQL(s"select $selectString")
  override protected val updateSQL: SqlQuery = SQL(
    """
      |update payment
      |set
      |payer = {payer},
      |cashier = {cashier},
      |amount = {amount},
      |description = {description}
      |where id = {id}
    """.stripMargin
  )
  override protected val selectBySQL: SqlQuery = SQL(
    s"""
       |select
       |$selectString
       |where payer = {id}
      """.stripMargin
  )
  private val selectByOrgSQL: SqlQuery = SQL(
    s"""
       |select
       |$selectString
       |where org_id = {id}
      """.stripMargin
  )
}
