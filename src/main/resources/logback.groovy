import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.FileAppender

import static ch.qos.logback.classic.Level.ALL
import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.INFO
import ch.qos.logback.core.ConsoleAppender

String path = null
if (System.getProperty("catalina.base") != null) {
    path = System.getProperty("catalina.base") + "/logs";
} else if (System.getProperty("jetty.logs") != null) {
    path = System.getProperty("jetty.logs");
}

System.out.println("path=" + path)

if (path != null) {
    appender("FILE", FileAppender) {
        file = path + "/gtasksdaemon.log"
        append = true
        encoder(PatternLayoutEncoder) {
            pattern = "%d [%thread] %-5level %logger{35} - %msg%n"
        }
    }
    logger("org.relgames", DEBUG)
    root(INFO, ["FILE"])
} else {
    appender("STDOUT", ConsoleAppender) {
        encoder(PatternLayoutEncoder) {
            pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
        }
    }
    root(ALL, ["STDOUT"])
}

