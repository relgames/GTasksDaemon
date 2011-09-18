package org.relgames.gtasksdaemon.utils

import org.slf4j.LoggerFactory
import java.util.Properties

trait Logging {
  val log = LoggerFactory.getLogger(this.getClass)
}

object Configuration extends Properties {
  load(getClass.getClassLoader.getResourceAsStream("auth.properties"))
}