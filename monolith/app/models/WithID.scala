package models

import anorm.NamedParameter

/**
  * Created by ryan on 3/24/16.
  */
trait WithID {
  val idOpt: Option[Long]
  def getIDParam: Option[NamedParameter] = idOpt map { 'id -> _ }
}
