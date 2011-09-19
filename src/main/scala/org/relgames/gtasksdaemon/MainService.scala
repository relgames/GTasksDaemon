package org.relgames.gtasksdaemon

import utils.Logging
object MainService extends Logging {
  Jul.replaceWithSLF4J()

  def process() {
    log.info("Start")

    GoogleOAuth2.login()

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
    GoogleOAuth2.login()

    //log.info("Calendar tasks: {}", GCalendar.tasksForToday)
    //log.info("Tasks: {}", GTasks.tasks)
    process()
  }

}