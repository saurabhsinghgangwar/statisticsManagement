package com.n26.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;

import com.n26.response.StatisticResponse;
import org.junit.Assert;
import org.junit.Test;

public class CalculationServiceTest {
    private final static  int scale = 2;
    private final static RoundingMode roundingMode = RoundingMode.HALF_UP ;

    @Test
    public void testConstructor() {
        CalculationService calculationService = new CalculationService();
        Assert.assertNotNull("calculationService can not be null", calculationService);
    }

    @Test
    public void testCloneStatisticResponse() {
        StatisticResponse statistics = StatisticResponse.newBuilder().withCount(2).build();
        StatisticResponse clone = CalculationService.cloneStatistic(statistics);
        Assert.assertEquals("Count not matched", statistics.getCount(), clone.getCount());
        Assert.assertEquals(statistics.getSum().compareTo( clone.getSum()) , 0 );
        Assert.assertEquals(statistics.getAvg().compareTo( clone.getAvg()), 0 );
        Assert.assertEquals(statistics.getMin().compareTo( clone.getMin()), 0 );
        Assert.assertEquals(statistics.getMax().compareTo( clone.getMax()), 0 );
    }

    @Test
    public void testAddStatisticResponseFirst() {
        StatisticResponse statistics = StatisticResponse.newBuilder().build();
        BigDecimal amount = new BigDecimal(1.20).setScale(scale,roundingMode);
        CalculationService.addStatistic(statistics, amount);

        Assert.assertEquals("Count not matched", statistics.getCount(), 1);
        Assert.assertEquals(statistics.getSum().compareTo(amount) , 0 );
        Assert.assertEquals(statistics.getAvg().compareTo( amount), 0 );
        Assert.assertEquals(statistics.getMin().compareTo( amount), 0 );
        Assert.assertEquals(statistics.getMax().compareTo( amount), 0 );
    }

    @Test
    public void testAddStatisticResponse() {
        StatisticResponse statistics = StatisticResponse.newBuilder().
                withCount(2).
                withSum(BigDecimal.valueOf(10)).
                withAvg(BigDecimal.valueOf(5)).
                withMin(BigDecimal.valueOf(10)).
                withMax(BigDecimal.valueOf(10))
                .build();
        BigDecimal amount = new BigDecimal(5);
        CalculationService.addStatistic(statistics, amount);

        Assert.assertEquals("Count not matched", statistics.getCount(), 3);
        Assert.assertEquals(statistics.getSum().compareTo(BigDecimal.valueOf(15) ), 0 );
        Assert.assertEquals(statistics.getAvg().compareTo( BigDecimal.valueOf(5)), 0 );
        Assert.assertEquals(statistics.getMin().compareTo( BigDecimal.valueOf(5)), 0 );
        Assert.assertEquals(statistics.getMax().compareTo( BigDecimal.valueOf(10)), 0 );
    }

    @Test
    public void testAddStatisticResponseFrom() {
        StatisticResponse to = StatisticResponse.newBuilder().build();
        StatisticResponse from =StatisticResponse.newBuilder().
                withCount(2).
                withSum(BigDecimal.valueOf(10)).
                withAvg(BigDecimal.valueOf(5)).
                withMin(BigDecimal.valueOf(10)).
                withMax(BigDecimal.valueOf(10))
                .build();
        CalculationService.addStatistic(to, from);

        Assert.assertEquals("Count not matched", to.getCount(), from.getCount());
        Assert.assertEquals(to.getSum().compareTo( from.getSum()) , 0 );
        Assert.assertEquals(to.getAvg().compareTo( from.getAvg()), 0 );
        Assert.assertEquals(to.getMin().compareTo( from.getMin()), 0 );
        Assert.assertEquals(to.getMax().compareTo( from.getMax()), 0 );
    }

    @Test
    public void testAddStatisticResponseFrom2() {
        StatisticResponse to  =StatisticResponse.newBuilder().
                withCount(2).
                withSum(BigDecimal.valueOf(10)).
                withAvg(BigDecimal.valueOf(5)).
                withMin(BigDecimal.valueOf(10)).
                withMax(BigDecimal.valueOf(10))
                .build();
        StatisticResponse from =StatisticResponse.newBuilder().
                withCount(2).
                withSum(BigDecimal.valueOf(10)).
                withAvg(BigDecimal.valueOf(5)).
                withMin(BigDecimal.valueOf(10)).
                withMax(BigDecimal.valueOf(10))
                .build();
        CalculationService.addStatistic(to, from);

        Assert.assertEquals("Count not matched", to.getCount(), 4);
        Assert.assertEquals(to.getSum().compareTo(BigDecimal.valueOf(20) ), 0 );
        Assert.assertEquals(to.getAvg().compareTo( BigDecimal.valueOf(5)), 0 );
        Assert.assertEquals(to.getMin().compareTo( BigDecimal.valueOf(10)), 0 );
        Assert.assertEquals(to.getMax().compareTo( BigDecimal.valueOf(10)), 0 );
    }

    @Test
    public void testRemove() {
        StatisticResponse statistics = StatisticResponse.newBuilder().
                withCount(6).
                withSum(BigDecimal.valueOf(6)).
                withAvg(BigDecimal.valueOf(1)).
                withMin(BigDecimal.valueOf(0.5)).
                withMax(BigDecimal.valueOf(1.5))
                .build();
        StatisticResponse clone = StatisticResponse.newBuilder().withCount(3).
                withSum(BigDecimal.valueOf(3)).
                withAvg(BigDecimal.valueOf(1)).
                withMin(BigDecimal.valueOf(0.2)).
                withMax(BigDecimal.valueOf(1.8))
                .build();

        CalculationService.remove(statistics, clone, Arrays.asList(BigDecimal.valueOf(0.5), BigDecimal.valueOf(1.8)));

        Assert.assertEquals("Count not matched", statistics.getCount(), 3);
        Assert.assertEquals(statistics.getSum().compareTo(BigDecimal.valueOf(3) ), 0 );
        Assert.assertEquals(statistics.getAvg().compareTo( BigDecimal.valueOf(1)), 0 );
        Assert.assertEquals(statistics.getMin().compareTo( BigDecimal.valueOf(0.5)), 0 );
        Assert.assertEquals(statistics.getMax().compareTo( BigDecimal.valueOf(1.8)), 0 );
    }

    @Test
    public void testRemoveZero() {
        StatisticResponse statistics = StatisticResponse.newBuilder().
                withCount(6).
                withSum(BigDecimal.valueOf(6)).
                withAvg(BigDecimal.valueOf(1)).
                withMin(BigDecimal.valueOf(0.5)).
                withMax(BigDecimal.valueOf(1.5))
                .build();
        StatisticResponse clone = CalculationService.cloneStatistic(statistics);

        CalculationService.remove(statistics, clone, new ArrayList<>());
        Assert.assertEquals("Count not matched", statistics.getCount(), 0);
        Assert.assertEquals(statistics.getSum().compareTo(BigDecimal.valueOf(0) ), 0 );
        Assert.assertEquals(statistics.getAvg().compareTo( BigDecimal.valueOf(0)), 0 );
        Assert.assertEquals(statistics.getMin().compareTo( BigDecimal.valueOf(0)), 0 );
        Assert.assertEquals(statistics.getMax().compareTo( BigDecimal.valueOf(0)), 0 );
    }

    @Test
    public void testRemoveOne() {
        StatisticResponse statistics = StatisticResponse.newBuilder().
                withCount(2).
                withSum(BigDecimal.valueOf(6)).
                withAvg(BigDecimal.valueOf(3)).
                withMin(BigDecimal.valueOf(0.5)).
                withMax(BigDecimal.valueOf(4))
                .build();
        StatisticResponse clone = StatisticResponse.newBuilder().withCount(1).
                withSum(BigDecimal.valueOf(3)).
                withAvg(BigDecimal.valueOf(3)).
                withMin(BigDecimal.valueOf(3)).
                withMax(BigDecimal.valueOf(3))
                .build();

        CalculationService.remove(statistics, clone, new ArrayList<>());

        Assert.assertEquals("Count not matched", statistics.getCount(), 1);
        Assert.assertEquals(statistics.getSum().compareTo(BigDecimal.valueOf(3) ), 0 );
        Assert.assertEquals(statistics.getAvg().compareTo( BigDecimal.valueOf(3)), 0 );
        Assert.assertEquals(statistics.getMin().compareTo( BigDecimal.valueOf(3)), 0 );
        Assert.assertEquals(statistics.getMax().compareTo( BigDecimal.valueOf(3)), 0 );
    }
}
