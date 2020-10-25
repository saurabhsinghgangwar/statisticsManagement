package com.n26.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;

import com.n26.response.StatisticResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class StatisticsServiceTest {

    @InjectMocks
    private StatisticsService statisticsService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddStatistics()  {
        StatisticResponse oldStat = statisticsService.getStatistics();

        BigDecimal amount = BigDecimal.valueOf(1.2);
        statisticsService.addStatistics(amount, Instant.now(Clock.systemUTC()).getEpochSecond());

        StatisticResponse newStat = statisticsService.getStatistics();
        Assert.assertEquals("Count not matched", oldStat.getCount() + 1, newStat.getCount());
    }

    @Test
    public void testOutOfRangeStatistics()  {
        StatisticResponse oldStat = statisticsService.getStatistics();

        BigDecimal amount = BigDecimal.valueOf(1.2);
        statisticsService.addOutOfRangeStatistics(amount, Instant.now(Clock.systemUTC()).getEpochSecond() + 1);

        StatisticResponse newStat = statisticsService.getStatistics();
        Assert.assertEquals("Count not matched", oldStat.getCount(), newStat.getCount());
    }

    @Test
    public  void  testDeleteTransaction(){

        BigDecimal amount = BigDecimal.valueOf(1.2);
        statisticsService.addStatistics(amount, Instant.now(Clock.systemUTC()).getEpochSecond());
        StatisticResponse oldStat = statisticsService.getStatistics();
        statisticsService.deleteTransitions();
        StatisticResponse newStat = statisticsService.getStatistics();
        Assert.assertNotEquals("Count  matched after delete", oldStat.getCount(), newStat.getCount());

    }

}
