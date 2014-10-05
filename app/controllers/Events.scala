package controllers

import play.api.data.Form
import play.api.data.Forms._
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

  val searchKeywordForm: Form[SearchKeyword] = Form {
    mapping(
      "q" -> text
    )(SearchKeyword.apply)(SearchKeyword.unapply)
  }

  def searchEventsByKeyword = Action.async { implicit request =>
    val keyword = searchKeywordForm.bindFromRequest.get

    collection
      .find(Json.obj("$text" -> Json.obj("$search" -> keyword.value)))
      .cursor[JsObject]
      .collect[List]()
      .map(JsArray)
      .map(Ok(_))
  }
}
