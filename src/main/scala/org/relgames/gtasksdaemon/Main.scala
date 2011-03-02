package org.relgames.gtasksdaemon

import utils.{HttpClient, Logging}

object Main extends Logging {
  def main(args: Array[String]):Unit = {
    log.info("Start")


    new GTasks().login()
    //val taskList = tasks()
    //log.info("Tasks: {}", taskList)

    //val httpClient = new HttpClient()
    //Http.cookies = Map("GALX"->"test", "cookie2"->"value2")
    //val res = Http.post("http://localhost:8090", Map("param1"->"value1", "param2"->"value2"))
    //log.debug("Cookies:{}\nResponse:\n{}", Http.cookies, res)

    log.info("Done")
  }

}