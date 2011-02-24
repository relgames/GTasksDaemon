package org.relgames.gtasksdaemon

import java.util.Properties
import org.slf4j.LoggerFactory
import scala.compat.Platform.currentTime
import org.apache.http.impl.client.{BasicResponseHandler, DefaultHttpClient}
import org.apache.http.message.BasicNameValuePair
import collection.JavaConversions._
import org.apache.http.client.methods.{HttpPost, HttpGet}
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.protocol.HTTP
import org.apache.http.client.HttpResponseException

trait Logging {
  val log = LoggerFactory.getLogger(this.getClass)
}

object Main extends Logging {
  val authProps = new Properties()
  authProps.load(this.getClass.getClassLoader.getResourceAsStream("auth.properties"))

  val username = authProps.getProperty("username")
  val password = authProps.getProperty("password")

  val httpClient = new DefaultHttpClient()
  val toStringResponseHandler = new BasicResponseHandler()

  def login():Unit = {
    log.info("Username is {}", username)

    val response = httpClient.execute(new HttpGet("https://www.google.com/accounts/ServiceLogin"), toStringResponseHandler)

    val cookies =  httpClient.getCookieStore.getCookies
    log.info("Cookies: {}", cookies)

    val galx = httpClient.getCookieStore.getCookies.find(_.getName=="GALX").getOrElse{
      log.error("Can't find cookie, response: {}", response)
      throw new RuntimeException("Can't find cookie")
    }.getValue

    log.info("GALX = {}", galx)

    val loginParams = List(
      new BasicNameValuePair("Email", username),
      new BasicNameValuePair("Passwd", password),
      new BasicNameValuePair("continue", "https://mail.google.com/tasks/m"),
      new BasicNameValuePair("GALX", galx)
    )

    val httpPost = new HttpPost("https://www.google.com/accounts/ServiceLoginAuth")
    httpPost.setEntity(new UrlEncodedFormEntity(loginParams, HTTP.UTF_8))

    httpClient.execute(httpPost)
  }

  def process():Unit = {
    log.info("Start")

    login()

    log.info("Done")
  }

  def main(args: Array[String]) = process

}