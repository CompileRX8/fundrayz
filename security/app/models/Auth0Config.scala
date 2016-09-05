package models

import com.google.inject.{AbstractModule, Provides}
import play.api.Configuration

/**
  * Created by ryan on 4/3/16.
  */
case class Auth0Config(secret: String, clientId: String, callbackURL: String, domain: String)

class Auth0ConfigFactory extends AbstractModule {
  val config = Configuration()

  @Provides
  def provideAuth0Config = Auth0Config(
    config.getString("auth0.clientSecret").get,
    config.getString("auth0.clientId").get,
    config.getString("auth0.callbackURL").get,
    config.getString("auth0.domain").get
  )

  override def configure(): Unit = {}
}
