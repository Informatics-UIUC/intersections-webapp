package models

import org.joda.time.DateTime
import play.api.libs.json.Json

case class SearchByKeywordAndTime(keyword: Option[String], startDate: Option[DateTime], endDate: Option[DateTime])

object SearchByKeywordAndTime {

  implicit val searchFormat = Json.format[SearchByKeywordAndTime]

}
