package org.relgames.gtasksdaemon

import java.util.Properties
import org.slf4j.LoggerFactory
import scala.compat.Platform.currentTime


trait Logging {
  val log = LoggerFactory.getLogger(this.getClass)
}

object Main extends Logging {
  val executionStart: Long = currentTime

  def main(args: Array[String]) {
    log.info("Start")

    val authProps = new Properties()
    authProps.load(this.getClass.getClassLoader.getResourceAsStream("auth.properties"))

    val username = authProps.getProperty("username")
    val password = authProps.getProperty("password")
    log.info("Username is {}", username)

    log.info("Done")
  }
}