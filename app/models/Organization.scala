package models

/**
  * Created by ryan on 12/24/15.
  */
case class Organization(id: Option[Int], name: String)

object Organization {
  private val nextOrgId: Iterator[Int] = new Iterator[Int] {
    private var n = 1

    override def hasNext: Boolean = true

    override def next: Int = {
      val i = n
      n += 1
      i
    }
  }

  private var orgs: List[Organization] = List()

  def create(name: String): Organization = {
    val org = Organization(Some(nextOrgId.next), name)
    orgs :+= org
    org
  }

  def get(id: Int): Option[Organization] = {
    orgs find {
      _.id.get == id
    }
  }

  def update(org: Organization): Option[Organization] = {
    org.id flatMap { id =>
      get(id) map { foundOrg =>
        val newOrg = Organization(foundOrg.id, org.name)
        val splitOrgs = orgs splitAt (orgs indexWhere {
          _.id.get == id
        })
        orgs = (splitOrgs._1 :+ newOrg) ++ splitOrgs._2.tail
        newOrg
      }
    }
  }

  def delete(org: Organization): Option[Organization] = {
    org.id flatMap { id =>
      get(id) map { foundOrg =>
        val splitOrgs = orgs splitAt (orgs indexWhere {
          _.id.get == id
        })
        orgs = splitOrgs._1 ++ splitOrgs._2.tail
        foundOrg
      }
    }
  }
}
