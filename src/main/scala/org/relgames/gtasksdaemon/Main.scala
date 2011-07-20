package org.relgames.gtasksdaemon

import utils.Logging
import org.joda.time._
object Main extends Logging {
  def main(args: Array[String]) {
    log.info("Start")

    log.info("Current tasks: {}", GTasks.tasks)
    log.info("Calendar tasks: {}", GCalendar.tasksForToday)

    log.info("Done")
  }

}