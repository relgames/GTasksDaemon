package org.relgames.gtasksdaemon

import java.util.Properties
import utils.{Http, DTDFix, Logging}
import xml.XML
import org.apache.http.client.HttpResponseException

object GTasks extends Logging {
  val authProps = new Properties()
  authProps.load(this.getClass.getClassLoader.getResourceAsStream("auth.properties"))

  val username = authProps.getProperty("username")
  val password = authProps.getProperty("password")

  val loginUrl = "https://www.google.com/accounts/ServiceLogin"
  val authUrl = "https://www.google.com/accounts/ServiceLoginAuth"
  val tasksUrl = "https://mail.google.com/tasks/m"

  def login():Unit = {
    log.info("Username is {}", username)

    Http.get(loginUrl)

    val galx = Http.cookies.find(_.getName=="GALX").getOrElse{
      throw new RuntimeException("Can't find cookie!")
    }.getValue

    log.info("GALX = {}", galx)

    try {
      Http.post(authUrl, Map(
        "Email" -> username,
        "Passwd" -> password,
        "continue" -> tasksUrl,
        "GALX" -> galx
      ))
    } catch {
      case e:HttpResponseException if (e.getStatusCode==302) => {}
    }


    if (Http.cookies.size < 2) {
      throw new RuntimeException("Login failed!")
    }

    log.info("Logged in")
  }

  def tasksXML() = {
    var tasksRaw = Http.get(tasksUrl)

    if (!tasksRaw.contains("<title>Tasks</title>")) {
      log.info("Page title is not Tasks, trying to login...")
      login()

      tasksRaw = Http.get(tasksUrl)
      log.trace("Tasks response:\n{}", tasksRaw)
      if (!tasksRaw.contains("<title>Tasks</title>")) {
        log.error("Logged in, but response is wrong:\n{}", tasksRaw)
        throw new RuntimeException("Logged in, but response is wrong")
      }
    }

    XML.withSAXParser(DTDFix.parser).loadString(tasksRaw)
  }

  def tasks():Seq[String] = {
    val xml = tasksXML
    val nodes = (xml\\"td").filter(el => (el\"@class").text == "text").map(_.text).filter(_.length>0)
    nodes
  }

  def addTask(task: String):Unit = {
    val xml = tasksXML

    val securityToken = ( (xml\\"input").filter(el => (el\"@name").text == "security_token")(0) \ "@value" ).text
    log.info("Security token = {}", securityToken)

    val pid = ( (xml\\"option").filter(el => (el\"@selected").text == "selected")(0) \ "@value" ).text
    log.info("Pid = {}", pid)

    try {
      Http.post(tasksUrl, Map(
        "actt" -> "create_tasks",
        "numa" -> "1",
        "pid" -> pid,
        "security_token" -> securityToken,
        "tkn1" -> task
      ))
    } catch {
      case e:HttpResponseException if (e.getStatusCode==302) => {}
    }


  }

}