package models.security

import javax.inject.Inject

import play.api.Configuration

/**
  * Created by ryan on 4/3/16.
  */
class Auth0Config @Inject()(config: Configuration) {
  def secret: String = { config.getString("auth0.clientSecret").get }
  def clientId: String = { config.getString("auth0.clientId").get }
  def callbackURL: String = { config.getString("auth0.callbackURL").get }
  def domain: String = { config.getString("auth0.domain").get }
}
