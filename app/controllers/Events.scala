package controllers

import org.joda.time.DateTimeZone
import play.api.data.Form
import play.api.data.Forms._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import reactivemongo.bson.BSONDateTime


object Events extends Controller with MongoController {

  def collection: JSONCollection = db collection "fb_events"

  import models._

  def findEventsByKeyword(keyword: String) = Action.async {
    collection
      .find(Json.obj("$text" -> Json.obj("$search" -> keyword)))
      .cursor[Event]
      .collect[List]()
      .map(Json.toJson(_))
      .map(Ok(_))
  }

  val searchByKeywordForm: Form[SearchByKeyword] = Form {
    mapping(
      "q" -> text
    )(SearchByKeyword.apply)(SearchByKeyword.unapply)
  }

  val searchByTimeForm: Form[SearchByTime] = Form {
    mapping(
      "start" -> optional(jodaDate("yyyy-MM-dd'T'HH:mm:ss.SSSZ", DateTimeZone.UTC)),
      "end" -> optional(jodaDate("yyyy-MM-dd'T'HH:mm:ss.SSSZ", DateTimeZone.UTC))
    )(SearchByTime.apply)(SearchByTime.unapply)
  }

  def searchEventsByKeyword = Action.async { implicit request =>
    val keyword = searchByKeywordForm.bindFromRequest.get

    collection
      .find(Json.obj("$text" -> Json.obj("$search" -> keyword.value)))
      .cursor[JsObject]
      .collect[List]()
      .map(JsArray)
      .map(Ok(_))
  }

  def searchEventsByTime = Action.async { implicit request =>
    val searchByTime = searchByTimeForm.bindFromRequest.get

    import play.modules.reactivemongo.json.BSONFormats._

    val start = searchByTime.start.map(dt =>
      Json.obj("start_time" -> Json.obj("$gte" -> BSONDateTime(dt.getMillis)))
    )

    val end = searchByTime.end.map(dt =>
      Json.obj("end_time" -> Json.obj("$lt" -> BSONDateTime(dt.getMillis)))
    )

    val query = Json.obj("$and" -> JsArray(List(start, end).collect {
      case Some(dt) => dt
    }))

    collection
      .find(query)
      .cursor[JsObject]
      .collect[List]()
      .map(JsArray)
      .map(Ok(_))
  }
}
