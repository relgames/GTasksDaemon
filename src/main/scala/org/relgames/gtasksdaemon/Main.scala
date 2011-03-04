package org.relgames.gtasksdaemon

import utils.Logging

object Main extends Logging {
  val gtasks = new GTasks

  def main(args: Array[String]):Unit = {
    log.info("Start")


    gtasks.login()
    val taskList = gtasks.tasks()
    log.info("Tasks: {}", taskList)

    log.info("Done")
  }

}