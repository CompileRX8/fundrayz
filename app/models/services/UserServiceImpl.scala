package models.services

import javax.inject.Inject

import models.daos.security.{UserDAO, UserRoleDAO}

/**
  * Created by ryan on 4/11/16.
  */
class UserServiceImpl @Inject()(userDAO: UserDAO, userRoleDAO: UserRoleDAO) extends UserService {

}
