package org.relgames.gtasksdaemon

import java.util.Properties
import collection.JavaConversions._
import java.net.{CookieManager, CookieHandler}
import io.Source
import utils.{Http, DTDFix, Logging}
import xml.XML

object GTasks extends Logging {
  val authProps = new Properties()
  authProps.load(this.getClass.getClassLoader.getResourceAsStream("auth.properties"))

  val username = authProps.getProperty("username")
  val password = authProps.getProperty("password")

  val loginUrl = "https://www.google.com/accounts/ServiceLogin"
  val authUrl = "https://www.google.com/accounts/ServiceLoginAuth"
  val tasksUrl = "https://mail.google.com/tasks/m"

  val cookieManager = new CookieManager()
  CookieHandler.setDefault(cookieManager);

  def login():Unit = {
    log.info("Username is {}", username)

    cookieManager.getCookieStore.removeAll

    var response = Source.fromURL(loginUrl).mkString
    log.debug("Login page:\n{}", response)

    log.info("Cookies: {}", cookieManager.getCookieStore.getCookies)

    val galx = cookieManager.getCookieStore.getCookies.find(_.getName=="GALX").getOrElse{
      log.error("Can't find cookie, response:\n{}", response)
      throw new RuntimeException("Can't find cookie")
    }.getValue

    log.info("GALX = {}", galx)

    response = Http.post(authUrl, Map(
      "Email" -> username,
      "Passwd" -> password,
      "continue" -> tasksUrl,
      "GALX" -> galx
    ))

    log.debug("Auth content:\n{}", response)

    log.info("Cookies: {}", cookieManager.getCookieStore.getCookies)
    if (cookieManager.getCookieStore.getCookies.length<2) {
      log.error("Login failed! Response:\n{}", response)
      throw new RuntimeException("Login failed")
    }

    log.info("Logged in")
  }

  def tasks():Seq[String] = {
    var tasksRaw = Source.fromURL(tasksUrl).mkString
    log.debug("Tasks response:\n{}", tasksRaw)

    if (!tasksRaw.contains("<title>Tasks</title>")) {
      log.info("Page title is not Tasks, trying to login...")
      login()

      tasksRaw = Source.fromURL(tasksUrl).mkString
      log.debug("Tasks response:\n{}", tasksRaw)
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