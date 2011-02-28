package org.relgames.gtasksdaemon

import utils.{Http, Logging}

object Main extends Logging {
  def main(args: Array[String]):Unit = {
    log.info("Start")

    //GTasks.login()
    //val taskList = tasks()
    //log.info("Tasks: {}", taskList)

    val res = Http.get("https://www.google.com/accounts/ServiceLogin")
    log.debug("Cookies:{}\nResponse:\n{}", Http.cookies, res)

    log.info("Done")
  }

}