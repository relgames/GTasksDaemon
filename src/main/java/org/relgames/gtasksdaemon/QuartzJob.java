package org.relgames.gtasksdaemon;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Oleg Poleshuk
 */
public class QuartzJob implements Job{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Calling MainService.process()");
        //MainService.process();
    }
}
