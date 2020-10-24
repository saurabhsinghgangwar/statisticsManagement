package com.n26.service;

import com.n26.response.StatisticResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
public class StatisticsService {
    private final Logger logger = LoggerFactory.getLogger(StatisticsService.class);
    public static final long TRANSACTION_EXPIRY_TIME= 60;
    private static final StatisticResponse latest = StatisticResponse.newBuilder().build();
    private static final Map<Long, StatisticResponse> statisticsMap = new ConcurrentHashMap<>();
    private static final Map<Long, StatisticResponse> outOfRangeStatisticsMap = new ConcurrentHashMap<>();

    private final ReentrantLock lock = new ReentrantLock();

    public void addStatistics(BigDecimal amount, long timestamp) {
        lock.lock();
        try {
            CalculationService.addStatistic(latest, amount);
            logger.info("current Statistics " + latest) ;
        } finally {
            lock.unlock();
        }
        StatisticResponse statistics = statisticsMap.computeIfAbsent(timestamp, key -> StatisticResponse.newBuilder().build());
        CalculationService.addStatistic(statistics, amount);
    }
    /**
     * returns current statistics
     *
     * @return
     */
    public StatisticResponse getStatistics() {
        lock.lock();
        try {
            return CalculationService.cloneStatistic(latest);
        } finally {
            lock.unlock();
        }
    }

    /**
     * The periodic job to remove old transaction records
     *
     */
    @Scheduled(cron = "* * * * * ?")
    private void removeOldRecords() {
        long currentEpoch = Instant.now(Clock.systemUTC()).getEpochSecond();

        long before60seconds = currentEpoch - TRANSACTION_EXPIRY_TIME;
        StatisticResponse statistics = statisticsMap.remove(before60seconds);
        if (statistics == null) {
            return;
        }

        logger.info("Triggered at {} for {}", currentEpoch, before60seconds);

        List<BigDecimal> maxMinAmountList = statisticsMap.entrySet().parallelStream().map(Map.Entry::getValue)
                .flatMap(s -> Arrays.asList(s.getMin(), s.getMax()).parallelStream())
                .collect(Collectors.toList());

        lock.lock();
        try {
            CalculationService.remove(latest, statistics, maxMinAmountList);
        } finally {
            lock.unlock();
        }

        logger.info("Size: {}, count: {}, avg: {}, sum: {}, max: {}, min: {}", statisticsMap.size(), latest.getCount(),
                latest.getAvg(), latest.getSum(), latest.getMax(), latest.getMin());
    }

    public void addOutOfRangeStatistics(BigDecimal amount, long timestamp) {
        StatisticResponse statistics = outOfRangeStatisticsMap.computeIfAbsent(timestamp,
                key -> StatisticResponse.newBuilder().build());
        CalculationService.addStatistic(statistics, amount);
    }
}
