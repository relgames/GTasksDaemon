package org.relgames.gtasksdaemon.utils

import org.slf4j.LoggerFactory
import javax.xml.parsers.{SAXParser, SAXParserFactory}
import org.apache.http.message.BasicNameValuePair
import org.apache.http.client.methods.{HttpPost, HttpGet}
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.protocol.HTTP
import collection.JavaConversions._
import org.apache.http.cookie.Cookie
import org.apache.http.impl.client.{DefaultHttpClient, BasicResponseHandler}
import java.util.Properties

trait Logging {
  val log = LoggerFactory.getLogger(this.getClass)
}

object Http extends Logging{
  private val httpClient = new DefaultHttpClient

  private val toStringResponseHandler = new BasicResponseHandler

  def cookies(): Seq[Cookie] = httpClient.getCookieStore.getCookies

  def get(url: String): String = {
    log.debug("GET {}", url)


    httpClient.execute(new HttpGet(url), toStringResponseHandler)
  }

  def post(url: String, params: Map[String, String]): String = {
    log.debug("POST {}", url)

    val loginParams = for ((k, v) <- params) yield new BasicNameValuePair(k, v)

    val httpPost = new HttpPost(url)
    httpPost.setEntity(new UrlEncodedFormEntity(loginParams.toList, HTTP.UTF_8))

    httpClient.execute(httpPost, toStringResponseHandler)
  }
}

/**
 * Contains definition of a custom SAX parser with a set of options to prevent external DTD loading
 * http://stackoverflow.com/questions/1096285/is-scala-java-not-respecting-w3-excess-dtd-traffic-specs
 * http://lampsvn.epfl.ch/trac/scala/ticket/2725
 */
object DTDFix {
  def parser:SAXParser = {
    val f = SAXParserFactory.newInstance()
    f.setNamespaceAware(false)
    //f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
    f.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
    f.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    f.newSAXParser()
  }
}

object Configuration extends Properties {
  load(getClass.getClassLoader.getResourceAsStream("auth.properties"))
}