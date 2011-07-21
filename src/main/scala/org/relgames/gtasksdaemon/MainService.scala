package org.relgames.gtasksdaemon

import utils.Logging
object MainService extends Logging {
  def process() {
    log.info("Start")

    val gTasks = GTasks.tasks
    val calendarTasks = GCalendar.tasksForToday
    log.info("Current tasks: {}", gTasks)
    log.info("Calendar tasks: {}", calendarTasks)

    for (possibleTask <- calendarTasks) {
      if (gTasks.find(_.equalsIgnoreCase(possibleTask)).isEmpty) {
        log.info("Adding task {}", possibleTask)
        GTasks.addTask(possibleTask)
      }
    }

    log.info("Done")
  }

  def main(args: Array[String]) {
    process()
  }

}