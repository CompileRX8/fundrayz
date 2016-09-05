package pg.extension

import com.github.tminglei.slickpg._
import play.api.libs.json._
import slick.driver.JdbcProfile
import slick.profile.Capability

trait MyPostgresDriver extends ExPostgresDriver
  with PgArraySupport
  with PgDateSupport
  with PgRangeSupport
  with PgPlayJsonSupport
  with PgSearchSupport
  with array.PgArrayJdbcTypes {
  override val pgjson = "jsonb"

  override protected def computeCapabilities: Set[Capability] = super.computeCapabilities + JdbcProfile.capabilities.insertOrUpdate

  override val api = MyAPI

  object MyAPI extends API
    with ArrayImplicits
    with DateTimeImplicits
    with RangeImplicits
    with JsonImplicits
    with SearchImplicits {
    implicit val strListTypeMapper = new SimpleArrayJdbcType[String]("text").to(_.toList)
    // implicit val beanJsonTypeMapper = MappedJdbcType.base[JBean, JsValue](Json.toJson(_), _.as[JBean])
    implicit val playJsonArrayTypeMapper =
    new AdvancedArrayJdbcType[JsValue](pgjson,
      (s) => utils.SimpleArrayUtils.fromString[JsValue](Json.parse)(s).orNull,
      (v) => utils.SimpleArrayUtils.mkString[JsValue](_.toString())(v)
    ).to(_.toList)
  }

  val plainAPI = new API with PlayJsonPlainImplicits
}

object MyPostgresDriver extends MyPostgresDriver

// I *think* this can be mapped directly to and from a jsonb field with the type mapper above
//case class JBean(name: String, count: Int)
//object JBean {
//  implicit val jbeanFmt = Json.format[JBean]
//  implicit val jbeanWrt = Json.writes[JBean]
//}
