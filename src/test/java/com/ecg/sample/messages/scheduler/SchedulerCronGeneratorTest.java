package com.ecg.sample.messages.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

/**
 * @author Mahdi Sharifi
 * @since 10/7/22
 */

@RunWith(SpringRunner.class)
@Slf4j
public class SchedulerCronGeneratorTest {

    private static final String START_DATE_FORM = "2022-10-10";
    private static final String SCHEDULER_24_HOURS_PERIOD_AT_23 = "0 0 23 * * *";

    @Test
    public void shouldGenerate11PMPerDay_whenCronSchedulerGeneratorIsCalled() {
        cronSchedulerGenerator(SCHEDULER_24_HOURS_PERIOD_AT_23, 10);
    }

    public void cronSchedulerGenerator(String paramScheduler, int index) {
        CronSequenceGenerator cronGen = new CronSequenceGenerator(paramScheduler);
        java.util.Date date = java.sql.Date.valueOf(START_DATE_FORM);
        for (int i = 0; i < index; i++) {
            date = cronGen.next(date);
            String dateTime = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format(date);
            assertTrue(dateTime.contains("11:00:00 PM"));
        }
    }
}