package models

import org.joda.time.DateTime

//TODO: Only needed for the findEventsByKeyword

case class Event(id: Long,
                 name: String,
                 description: Option[String],
                 startTime: DateTime,
                 endTime: Option[DateTime])

object Event {
  import play.api.libs.json._
  import play.api.libs.json.Reads._
  import play.api.libs.functional.syntax._

  val eventReads = (
      (__ \ "_id").read[Long] and
      (__ \ 'name).read[String] and
      (__ \ 'description).readNullable[String] and
      (__ \ 'start_time \ '$date).read[DateTime] and
      ((__ \ 'end_time \ '$date).readNullable[DateTime] orElse (__ \ 'end_time).readNullable[DateTime])
    )(Event.apply _)

  val eventWrites = (
      (__ \ "_id").write[Long] and
      (__ \ 'name).write[String] and
      (__ \ 'description).writeNullable[String] and
      (__ \ 'start_time \ '$date).write[DateTime] and
      (__ \ 'end_time \ '$date).writeNullable[DateTime]
    )(unlift(Event.unapply))

  implicit val eventFormat = Format(eventReads, eventWrites)
}