package org.relgames.gtasksdaemon.utils

import org.slf4j.LoggerFactory

trait Logging {
  val log = LoggerFactory.getLogger(this.getClass)
}
