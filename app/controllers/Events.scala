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

  val searchByKeywordAndTimeForm: Form[SearchByKeywordAndTime] = Form {
    mapping(
      "q" -> optional(text),
      "start" -> optional(jodaDate("yyyy-MM-dd'T'HH:mm:ss.SSSZ", DateTimeZone.UTC)),
      "end" -> optional(jodaDate("yyyy-MM-dd'T'HH:mm:ss.SSSZ", DateTimeZone.UTC))
    )(SearchByKeywordAndTime.apply)(SearchByKeywordAndTime.unapply)
  }

  def searchEvents = Action.async { implicit request =>
    import play.modules.reactivemongo.json.BSONFormats._

    val form = searchByKeywordAndTimeForm.bindFromRequest.get

    val keyword = form.keyword.map(keyword => Json.obj("$text" -> Json.obj("$search" -> keyword)))

    val start = form.startDate.map(dt =>
      Json.obj("start_time" -> Json.obj("$gte" -> BSONDateTime(dt.getMillis)))
    )

    val end = form.endDate.map(dt =>
      Json.obj(
        "$and" -> JsArray(List(
          Json.obj(
            "start_time" -> Json.obj("$lte" -> BSONDateTime(dt.getMillis))
          ),
          Json.obj(
            "$or" -> JsArray(List(
              Json.obj("end_time" -> Json.obj("$lt" -> BSONDateTime(dt.getMillis))),
              Json.obj("end_time" -> Json.obj("$exists" -> false))
            ))
          )
        ))
      )
    )

    val query = Json.obj("$and" -> JsArray(List(keyword, start, end).collect {
      case Some(q) => q
    }))

    collection
      .find(query)
      .cursor[JsObject]
      .collect[List]()
      .map(JsArray)
      .map(Ok(_))
  }
}
