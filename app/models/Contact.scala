package models

/**
  * Created by ryan on 12/24/15.
  */
case class Contact(id: Option[Int], userInfo: Option[User], orgs: List[Organization] = List())

object Contact {

}
