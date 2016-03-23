package models.services

import javax.inject.Inject

import models._
import models.daos.organization.{PaymentDAO, PurchaseDAO}
import models.daos.ModelDAO

import scala.concurrent.Future

/**
  * Created by ryan on 3/16/16.
  */
class OrganizationServiceImpl @Inject()(
                                         campaignDAO: ModelDAO[Campaign, Organization],
                                         eventDAO: ModelDAO[Event, Campaign],
                                         workScheduleDAO: ModelDAO[WorkSchedule, Event],
                                         contactDAO: ModelDAO[Contact, Organization],
                                         organizationDAO: ModelDAO[Organization, Organization],
                                         paymentDAO: PaymentDAO,
                                         purchaseDAO: PurchaseDAO
                                       ) extends OrganizationService {
  override def save(org: Organization): Future[Organization] = organizationDAO.save(org)
  override def findOrganization(id: Long): Future[Option[Organization]] = organizationDAO.find(id)
  override def listOrganizations: Future[List[Organization]] = organizationDAO.all

  override def save(campaign: Campaign): Future[Campaign] = campaignDAO.save(campaign)
  override def findCampaign(id: Long): Future[Option[Campaign]] = campaignDAO.find(id)
  override def findCampaignsForOrganization(org: Organization): Future[Option[List[Campaign]]] = campaignDAO.findBy(org)
  override def listCampaigns: Future[List[Campaign]] = campaignDAO.all

  override def save(event: Event): Future[Event] = eventDAO.save(event)
  override def findEvent(id: Long): Future[Option[Event]] = eventDAO.find(id)
  override def findEventsForCampaign(campaign: Campaign): Future[Option[List[Event]]] = eventDAO.findBy(campaign)
  override def listEvents: Future[List[Event]] = eventDAO.all

  override def save(workSchedule: WorkSchedule): Future[WorkSchedule] = workScheduleDAO.save(workSchedule)
  override def findWorkSchedule(id: Long): Future[Option[WorkSchedule]] = workScheduleDAO.find(id)
  override def findWorkSchedulesForEvent(event: Event): Future[Option[List[WorkSchedule]]] = workScheduleDAO.findBy(event)
  override def listWorkSchedules: Future[List[WorkSchedule]] = workScheduleDAO.all

  override def save(contact: Contact): Future[Contact] = contactDAO.save(contact)
  override def findContact(id: Long): Future[Option[Contact]] = contactDAO.find(id)
  override def findContactsForOrganization(org: Organization): Future[Option[List[Contact]]] = contactDAO.findBy(org)
  override def listContacts: Future[List[Contact]] = contactDAO.all

  override def save(payment: Payment): Future[Payment] = paymentDAO.save(payment)
  override def findPayment(id: Long): Future[Option[Payment]] = paymentDAO.find(id)
  override def findPaymentsForContact(contact: Contact): Future[Option[List[Payment]]] = paymentDAO.findBy(contact)
  override def findPaymentsForOrganization(org: Organization): Future[Option[List[Payment]]] = paymentDAO.findBy(org)
  override def listPayments: Future[List[Payment]] = paymentDAO.all

  override def save(purchase: Purchase): Future[Purchase] = purchaseDAO.save(purchase)
  override def findPurchase(id: Long): Future[Option[Purchase]] = purchaseDAO.find(id)
  override def findPurchasesForContact(contact: Contact): Future[Option[List[Purchase]]] = purchaseDAO.findBy(contact)
  override def findPurchasesForOrganization(org: Organization): Future[Option[List[Purchase]]] = purchaseDAO.findBy(org)
  override def findPurchasesForCampaign(campaign: Campaign): Future[Option[List[Purchase]]] = purchaseDAO.findBy(campaign)
  override def listPurchases: Future[List[Purchase]] = purchaseDAO.all
}
