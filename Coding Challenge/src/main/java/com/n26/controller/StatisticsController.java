package com.n26.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.n26.request.TransactionRequest;
import com.n26.response.StatisticResponse;
import com.n26.service.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

@RestController
public class StatisticsController {

    public static final long TRANSACTION_EXPIRY_TIME= 60;

    @Autowired
    private StatisticsService statisticsService ;

    private Logger logger = LoggerFactory.getLogger(StatisticsController.class) ;
    @GetMapping(path = "/statistics")
    public StatisticResponse getStatistics(HttpServletResponse response){
        response.setStatus(HttpStatus.OK.value());
        return statisticsService.getStatistics();
    }

    @PostMapping(path = "/transactions")
    public void doTransactions( @RequestBody TransactionRequest transaction , HttpServletResponse response){
        long epochTime = Instant.now(Clock.systemUTC()).getEpochSecond();
        if (transaction == null || transaction.getAmount().compareTo(new BigDecimal("0.0")) <=0 ) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        long timestamp = Instant.ofEpochMilli(transaction.getTimestamp().getTime()).atOffset(ZoneOffset.UTC).toEpochSecond();

        long diff = epochTime - timestamp;
         if (diff >= TRANSACTION_EXPIRY_TIME) {
            logger.info("Old Transaction");
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        // timestamp is greater than 1 minute,
        if (epochTime < timestamp) {
            logger.info("outOfRange timestamp - currentEpochTime: {}, timestamp: {}, diff: {}", epochTime, timestamp, diff);
            statisticsService.addFutureStatistics(transaction.getAmount(), timestamp);
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());

        } else {
            statisticsService.addStatistics(transaction.getAmount(), timestamp);
            response.setStatus(HttpServletResponse.SC_CREATED);
        }
    }

    @DeleteMapping (path = "/transactions")
    public void  deleteTransactions(HttpServletResponse response){
            statisticsService.deleteTransitions();
            response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public void handleHttpMessageNotReadableException(Exception ex , HttpServletResponse response){
        if(ex.getCause() instanceof  MismatchedInputException){
            response.setStatus( HttpStatus.BAD_REQUEST.value());
        }
        if(ex.getCause() instanceof InvalidFormatException){
            response.setStatus( HttpStatus.UNPROCESSABLE_ENTITY.value());
        }
    }
}
