package controllers

import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

object Events extends Controller with MongoController {

  def collection: JSONCollection = db collection "testimport"

  import models._

  def findEventsByKeyword(keyword: String) = Action.async {
    collection
      .find(Json.obj("$text" -> Json.obj("$search" -> keyword)))
      .cursor[Event]
      .collect[List]()
      .map(Json.toJson(_))
      .map(Ok(_))
  }

  def searchEventsByKeyword(keyword: String) = Action.async {
    collection
      .find(Json.obj("$text" -> Json.obj("$search" -> keyword)))
      .cursor[JsObject]
      .collect[List]()
      .map(JsArray)
      .map(Ok(_))
  }
}
