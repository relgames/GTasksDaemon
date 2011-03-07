package org.relgames.gtasksdaemon

import utils.Logging

object Main extends Logging {
  val gtasks = new GTasks

  def main(args: Array[String]):Unit = {
    log.info("Start")

    gtasks.login()

    log.info("Tasks: {}", gtasks.tasks())
    gtasks.addTask("New test task " + System.currentTimeMillis)
    log.info("Tasks: {}", gtasks.tasks())

    log.info("Done")
  }

}