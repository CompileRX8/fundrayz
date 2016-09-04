package models.services

import models._

import scala.concurrent.Future

/**
  * Created by ryan on 3/16/16.
  */
trait OrganizationService {
  def save(org: Organization): Future[Organization]
  def findOrganization(id: Long): Future[Option[Organization]]
  def listOrganizations: Future[List[Organization]]

  def save(event: Event): Future[Event]
  def findEvent(id: Long): Future[Option[Event]]
  def findEventsForOrganization(org: Organization): Future[Option[List[Event]]]
  def listEvents: Future[List[Event]]

  def save(workSchedule: WorkSchedule): Future[WorkSchedule]
  def findWorkSchedule(id: Long): Future[Option[WorkSchedule]]
  def findWorkSchedulesForEvent(event: Event): Future[Option[List[WorkSchedule]]]
  def listWorkSchedules: Future[List[WorkSchedule]]

  def save(contact: Contact): Future[Contact]
  def findContact(id: Long): Future[Option[Contact]]
  def findContactsForOrganization(org: Organization): Future[Option[List[Contact]]]
  def listContacts: Future[List[Contact]]

  def save(payment: Payment): Future[Payment]
  def findPayment(id: Long): Future[Option[Payment]]
  def findPaymentsForContact(contact: Contact): Future[Option[List[Payment]]]
  def findPaymentsForOrganization(org: Organization): Future[Option[List[Payment]]]
  def listPayments: Future[List[Payment]]

  def save(purchase: Purchase): Future[Purchase]
  def findPurchase(id: Long): Future[Option[Purchase]]
  def findPurchasesForContact(contact: Contact): Future[Option[List[Purchase]]]
  def findPurchasesForOrganization(org: Organization): Future[Option[List[Purchase]]]
  def listPurchases: Future[List[Purchase]]
}
