package org.relgames.gtasksdaemon

import javax.servlet.http.{HttpServlet, HttpServletRequest => HSReq, HttpServletResponse => HSResp}

class HelloServlet extends HttpServlet {
  override def doGet(req: HSReq, resp: HSResp) = {
    val hello =
      <html>
        <head>
          <title>Google Tasks Daemon</title>
        </head>
        <body>Google Tasks Daemon is up and running!</body>
      </html>

    resp.getWriter().print(hello)
  }
}