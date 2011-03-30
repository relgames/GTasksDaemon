package org.relgames.gtasksdaemon

import utils.{Configuration, Logging}
import collection.mutable.ListBuffer
import xml.XML
import java.net.URL

object GCalendar extends Logging {
  protected val calendars = (for (i <- 1 to 10) yield Configuration.getProperty("calc"+i)).filter(_ != null)

  log.info("calendars: {}", calendars)

  def tasks: Seq[String] = {
    calendars.flatMap{calendarUrl =>
      val xml = XML.load(new URL(calendarUrl))
      log.debug("xml: {}", xml)
      (xml \ "entry" \ "title").map(_.text)
    }
  }
}