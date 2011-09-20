package org.relgames.gtasksdaemon;

import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Oleg Poleshuk
 */
public class QuartzJob implements Job {
    public static DateTime lastExecution;

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final int TRY_COUNT = 5;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        int count = 0;
        while (true) {
            try {
                count++;
                log.info("Calling MainService.process(), attempt {}", count);
                MainService.process();
                lastExecution = new DateTime();
                break;
            } catch (Exception e) {
                log.error("Exception", e);
                if (count >= TRY_COUNT) {
                    log.warn("Reached TRY_COUNT, exiting");
                    break;
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new QuartzJob().execute(null);
    }
}
