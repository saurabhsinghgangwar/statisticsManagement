package com.n26.service;

import com.n26.response.StatisticResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;

@Service
public class CalculationService {

    private final static  int scale = 2;
    private final static RoundingMode roundingMode = RoundingMode.HALF_UP ;
    /**
     * clones a StatisticResponse object
     *
     * @param statistics object to clone
     * @return a new StatisticResponse object
     */
    public static StatisticResponse cloneStatistic(StatisticResponse statistics) {
        return StatisticResponse.newBuilder().withAvg(statistics.getAvg()).withCount(statistics.getCount())
                .withMax(statistics.getMax()).withMin(statistics.getMin()).withSum(statistics.getSum()).build();
    }

    /**
     * adds the amount to a StatisticResponse object
     *
     * @param to     object to add
     * @param amt the new amount
     */
    public static void addStatistic(StatisticResponse to, BigDecimal amt) {
        to.setCount(to.getCount() + 1);
        BigDecimal amount =amt.setScale(scale,roundingMode) ;
        if (to.getCount() == 1) {
            to.setSum(amount);
            to.setAvg(amount);
            to.setMin(amount);
            to.setMax(amount);
        } else {
            BigDecimal sum = to.getSum().add(amount).setScale(scale, roundingMode);
            to.setSum(sum);
            to.setAvg(sum.divide(BigDecimal.valueOf(to.getCount()),scale,roundingMode).setScale(scale, roundingMode));
            to.setMin(BigDecimal.valueOf(Math.min(to.getMin().doubleValue(), amount.doubleValue())).setScale(scale, roundingMode));
            to.setMax(BigDecimal.valueOf(Math.max(to.getMax().doubleValue(), amount.doubleValue())).setScale(scale, roundingMode));
        }
    }

    /**
     * adds the amount to a StatisticResponse object
     *
     * @param to   object to add
     * @param from the new amount
     */
    public static void addStatistic(StatisticResponse to, StatisticResponse from) {
        to.setCount(to.getCount() + from.getCount());
        if (to.getCount() == from.getCount()) {
            to.setSum(from.getSum().setScale(scale, roundingMode));
            to.setAvg(from.getAvg().setScale(scale, roundingMode));
            to.setMin(from.getMin().setScale(scale, roundingMode));
            to.setMax(from.getMax().setScale(scale, roundingMode));
        } else {
            BigDecimal sum = to.getSum().add(from.getSum()).setScale(scale, roundingMode);
            to.setSum(sum);
            to.setAvg(sum.divide(BigDecimal.valueOf(to.getCount()), scale, roundingMode));
            to.setMin(BigDecimal.valueOf(Math.min(to.getMin().doubleValue(), from.getMin().doubleValue())).setScale(scale, roundingMode));
            to.setMax(BigDecimal.valueOf(Math.max(to.getMax().doubleValue(), from.getMax().doubleValue())).setScale(scale, roundingMode));
        }
    }

    /**
     * removes expired statistics from total statistics
     *
     * @param from             object to remove
     * @param to               removing object
     * @param maxMinAmountList min and max amount values of all seconds
     */
    public static void remove(StatisticResponse from, StatisticResponse to, List<BigDecimal> maxMinAmountList) {
        BigDecimal sum = from.getSum().subtract(to.getSum())
                .setScale(scale, roundingMode);
        from.setCount(from.getCount() - to.getCount());
        from.setSum(sum);

        if (from.getCount() == 1) {
            from.setAvg(from.getSum().setScale(scale, roundingMode));
            from.setMin(from.getSum().setScale(scale, roundingMode));
            from.setMax(from.getSum().setScale(scale, roundingMode));
        } else if (from.getCount() > 0) {
            from.setAvg(sum.divide(BigDecimal.valueOf(from.getCount()) ,scale, roundingMode).setScale(scale, roundingMode));
            Supplier<DoubleStream> doubleStreamSupplier = () -> maxMinAmountList.parallelStream().mapToDouble(BigDecimal::doubleValue);
            from.setMin(BigDecimal.valueOf(doubleStreamSupplier.get().min().orElse(0.0)).setScale(scale, roundingMode));
            from.setMax(BigDecimal.valueOf(doubleStreamSupplier.get().max().orElse(0.0)).setScale(scale, roundingMode));
        } else {
            from.setAvg(BigDecimal.valueOf(0.0).setScale(scale, roundingMode));
            from.setMin(BigDecimal.valueOf(0.0).setScale(scale, roundingMode));
            from.setMax(BigDecimal.valueOf(0.0).setScale(scale, roundingMode));
        }
    }

}


