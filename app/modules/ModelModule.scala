package modules

import com.google.inject.{AbstractModule, Provides}
import models._
import models.daos._
import models.daos.organization._
import models.daos.security.{UserDAO, UserDAOImpl}
import models.services.{OrganizationService, OrganizationServiceImpl, UserService, UserServiceImpl}
import net.codingwell.scalaguice.ScalaModule
import play.api.Play

/**
  * Created by ryan on 3/16/16.
  */
class ModelModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[OrganizationService].to[OrganizationServiceImpl]
    bind[UserService].to[UserServiceImpl]
    bind[AbstractModelDAO[Organization, Organization]].to[OrganizationDAOImpl]
    bind[AbstractModelDAO[Event, Organization]].to[EventDAOImpl]
    bind[AbstractModelDAO[WorkSchedule, Event]].to[WorkScheduleDAOImpl]
    bind[AbstractModelDAO[Contact, Organization]].to[ContactDAOImpl]
    bind[UserDAO].to[UserDAOImpl]
    bind[PaymentDAO].to[PaymentDAOImpl]
    bind[PurchaseDAO].to[PurchaseDAOImpl]
  }

  @Provides
  def providesAuth0Config = {
    Auth0Config(
      Play.current.configuration.getString("auth0.clientSecret").get,
      Play.current.configuration.getString("auth0.clientId").get,
      Play.current.configuration.getString("auth0.callbackURL").get,
      Play.current.configuration.getString("auth0.domain").get
    )
  }
}
