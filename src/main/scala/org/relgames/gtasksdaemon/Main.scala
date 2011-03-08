package org.relgames.gtasksdaemon

import utils.Logging

object Main extends Logging {
  def main(args: Array[String]):Unit = {
    log.info("Start")

    GTasks.login()

    log.info("Tasks: {}", GTasks.tasks())
    GTasks.addTask("New test task " + System.currentTimeMillis)
    log.info("Tasks: {}", GTasks.tasks())

    log.info("Done")
  }

}