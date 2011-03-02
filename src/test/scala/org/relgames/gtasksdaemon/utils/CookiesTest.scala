package org.relgames.gtasksdaemon.utils

import org.junit.Assert._
import org.junit.{Before, Test}

class CookiesTest {
  var httpClient:HttpClient = null

  @Before
  def init() = {
    httpClient = new HttpClient
  }

  @Test
  def testParser() = {
    //assertEquals(("key", "value"), Http.parseCookie("key=value"))
    assertEquals(("key", "value"), httpClient.parseCookie("key=value;"))
    assertEquals(("key", "value"), httpClient.parseCookie("key=value;sdfsdfsdf=dfdfg"))
    assertEquals(("LSID", "s.BY|s.NL:DQAAAJwAAADyN21o3SBP9i_u3b2I7uSJorFQ7Mhw9IOD8cGiDJGkpmNu6VUeD3SoNHGLlkwYh4dEIk6_SfXOanOmh6bgnCvs02oVCFxAP-b-VSP18HIuAHgdmAtPXRAj1VY3x2aOXsmN5_sZMWofKcP5bb2ghQYyNsXTme-jkLKPrndhIjbI_r0Obpb9Fr2wo-W5xIBsNGnCuyEEEzhJN3PIRcAC6ThY"),
      httpClient.parseCookie("LSID=s.BY|s.NL:DQAAAJwAAADyN21o3SBP9i_u3b2I7uSJorFQ7Mhw9IOD8cGiDJGkpmNu6VUeD3SoNHGLlkwYh4dEIk6_SfXOanOmh6bgnCvs02oVCFxAP-b-VSP18HIuAHgdmAtPXRAj1VY3x2aOXsmN5_sZMWofKcP5bb2ghQYyNsXTme-jkLKPrndhIjbI_r0Obpb9Fr2wo-W5xIBsNGnCuyEEEzhJN3PIRcAC6ThY;Path=/accounts;Secure;HttpOnly") )
  }

  @Test
  def mkCookieTest() = {
    assertEquals("key=value", httpClient.mkCookieHeader( Map("key"->"value") ))
    assertEquals("key=value; key2=value2", httpClient.mkCookieHeader( Map("key"->"value", "key2"->"value2") ))
  }
}