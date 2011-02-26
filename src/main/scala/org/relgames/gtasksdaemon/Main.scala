package org.relgames.gtasksdaemon

import java.util.Properties
import collection.JavaConversions._
import java.net.{CookieManager, URL, CookieHandler}
import utils.Logging
import utils.EncodableMap._
import io.Source
import java.io.OutputStreamWriter

object Main extends Logging {
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
    log.info("Logging in...")
    log.info("Username is {}", username)

    var response = Source.fromURL(loginUrl).mkString
    log.debug("Login page:\n{}", response)

    log.info("Cookies: {}", cookieManager.getCookieStore.getCookies)

    val galx = cookieManager.getCookieStore.getCookies.find(_.getName=="GALX").getOrElse{
      log.error("Can't find cookie, response:\n{}", response)
      throw new RuntimeException("Can't find cookie")
    }.getValue

    log.info("GALX = {}", galx)

    val loginParams = Map(
      "Email" -> username,
      "Passwd" -> password,
      "continue" -> tasksUrl,
      "GALX" -> galx
    ).urlEncode


    val postConnection = new URL(authUrl).openConnection
    postConnection.setDoOutput(true)

    val writer = new OutputStreamWriter(postConnection.getOutputStream)
    writer.write(loginParams)
    writer.flush

    response = Source.fromInputStream(postConnection.getInputStream).mkString
    log.debug("Auth content:\n{}", response)

    log.info("Cookies: {}", cookieManager.getCookieStore.getCookies)
    if (cookieManager.getCookieStore.getCookies.length<2) {
      log.error("Login failed! Response:\n{}", response)
      throw new RuntimeException("Login failed")
    }

    log.info("Logged in")
  }

  def process():Unit = {
    log.info("Start")

    login()

    log.info("Done")
  }

  def main(args: Array[String]):Unit = process

}