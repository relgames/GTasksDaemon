package org.relgames.gtasksdaemon

import GTasks._
import utils.Logging

object Main extends Logging {
  def main(args: Array[String]):Unit = {
    log.info("Start")

    login()

    log.info("Done")
  }

}