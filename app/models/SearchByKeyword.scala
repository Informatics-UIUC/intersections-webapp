package models

import play.api.libs.json.Json

case class SearchByKeyword(value: String)

object SearchByKeyword {

  implicit val searchKeywordFormat = Json.format[SearchByKeyword]

}