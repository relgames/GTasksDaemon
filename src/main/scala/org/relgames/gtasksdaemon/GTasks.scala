package org.relgames.gtasksdaemon

import java.util.Properties
import utils.{Http, DTDFix, Logging}
import xml.XML
import org.apache.http.client.HttpResponseException

class GTasks extends Logging {
  val authProps = new Properties()
  authProps.load(this.getClass.getClassLoader.getResourceAsStream("auth.properties"))

  val username = authProps.getProperty("username")
  val password = authProps.getProperty("password")

  val loginUrl = "https://www.google.com/accounts/ServiceLogin"
  val authUrl = "https://www.google.com/accounts/ServiceLoginAuth"
  val tasksUrl = "https://mail.google.com/tasks/m"

  val httpClient = new Http

  def login():Unit = {
    log.info("Username is {}", username)

    var response = httpClient.get(loginUrl)
    log.trace("Login page:\n{}", response)

    val galx = httpClient.cookies.find(_.getName=="GALX").getOrElse{
      throw new RuntimeException("Can't find cookie!")
    }.getValue

    log.info("GALX = {}", galx)

    try {
      response = httpClient.post(authUrl, Map(
        "Email" -> username,
        "Passwd" -> password,
        "continue" -> tasksUrl,
        "GALX" -> galx
      ))
    } catch {
      case e:HttpResponseException if (e.getStatusCode==302) => {}
    }


    if (httpClient.cookies.size < 2) {
      throw new RuntimeException("Login failed!")
    }

    log.info("Logged in")
  }

  def tasks():Seq[String] = {
    var tasksRaw = httpClient.get(tasksUrl)
    log.trace("Tasks response:\n{}", tasksRaw)

    if (!tasksRaw.contains("<title>Tasks</title>")) {
      log.info("Page title is not Tasks, trying to login...")
      login()

      tasksRaw = httpClient.get(tasksUrl)
      log.trace("Tasks response:\n{}", tasksRaw)
      if (!tasksRaw.contains("<title>Tasks</title>")) {
        log.error("Logged in, but response is wrong:\n{}", tasksRaw)
        throw new RuntimeException("Logged in, but response is wrong")
      }
    }

    val tasksXml = XML.withSAXParser(DTDFix.parser).loadString(tasksRaw)
    val nodes = (tasksXml\\"td").filter(el => (el\"@class").text == "text").map(_.text).filter(_.length>0)

    nodes
  }

  def addTask():Unit = {

  }

}