package org.relgames.gtasksdaemon;

import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Oleg Poleshuk
 */
public class ExecutionServlet extends HttpServlet {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        try {
            new QuartzJob().execute(null);
        } catch (JobExecutionException e) {
            throw new ServletException("Can't execute QuartzJob", e);
        }

        response.getWriter().write("Done!");
    }
}
