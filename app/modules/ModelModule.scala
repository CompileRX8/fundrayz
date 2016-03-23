package modules

import com.google.inject.AbstractModule
import models._
import models.daos._
import models.daos.organization._
import models.services.{OrganizationService, OrganizationServiceImpl}
import net.codingwell.scalaguice.ScalaModule

/**
  * Created by ryan on 3/16/16.
  */
class ModelModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[OrganizationService].to[OrganizationServiceImpl]
    bind[ModelDAO[Organization, Organization]].to[OrganizationDAOImpl]
    bind[ModelDAO[Campaign, Organization]].to[CampaignDAOImpl]
    bind[ModelDAO[Event, Campaign]].to[EventDAOImpl]
    bind[ModelDAO[WorkSchedule, Event]].to[WorkScheduleDAOImpl]
    bind[ModelDAO[Contact, Organization]].to[ContactDAOImpl]
    bind[PaymentDAO].to[PaymentDAOImpl]
    bind[PurchaseDAO].to[PurchaseDAOImpl]
  }
}
