package models

import org.joda.time.DateTime
import play.api.libs.json.Json

case class SearchByTime(start: Option[DateTime], end: Option[DateTime])

object SearchByTime {

  implicit val timeSearchFormat = Json.format[SearchByTime]

}