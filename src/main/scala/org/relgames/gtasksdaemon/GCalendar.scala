package org.relgames.gtasksdaemon

import utils.{Configuration, Logging}
import xml.XML
import org.joda.time.{Seconds, DateTime, DateMidnight}
import java.net.{URLEncoder, URL}
import org.joda.time.format.DateTimeFormat

object GCalendar extends Logging {
  protected val calendars = (for (i <- 1 to 10) yield Configuration.getProperty("calc"+i)).filter(_ != null)

  log.info("calendars: {}", calendars)

  def tasksForToday: Seq[String] = {
    val dtf = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss") // RFC 3339 without timezone
    def enc(s: String) = URLEncoder.encode(s, "UTF-8")

    calendars.flatMap{calendarUrl =>
      val d1 = new DateMidnight
      val d2 = new DateTime(d1).plusDays(1).minus(Seconds.ONE);
      log.info("d1: {}; d2: {}", d1, d2);

      val urlForToday = calendarUrl + "?" + "start-min=" + enc(dtf.print(d1)) + "&start-max=" + enc(dtf.print(d2));
      log.info("Requesting URL: {}", urlForToday)
      val xml = XML.load(new URL(urlForToday))
      log.debug("xml: {}", xml)
      (xml \ "entry" \ "title").map(_.text)
    }
  }
}