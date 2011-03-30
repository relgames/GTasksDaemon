package org.relgames.gtasksdaemon

import io.Source
import book.scala.beginning.JSON
import utils.{JSON2, Logging}
import xml.XML

object Main extends Logging {
  def main(args: Array[String]):Unit = {
    log.info("Start")

    log.info("tasks: {}", GCalendar.tasks)

    log.info("Done")
  }

}