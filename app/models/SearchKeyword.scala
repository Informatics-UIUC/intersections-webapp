package models

import play.api.libs.json.Json

case class SearchKeyword(value: String)

object SearchKeyword {

  implicit val searchKeywordFormat = Json.format[SearchKeyword]

}