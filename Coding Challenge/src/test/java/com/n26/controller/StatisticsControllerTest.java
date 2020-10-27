package com.n26.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n26.request.TransactionRequest;
import com.n26.response.StatisticResponse;
import com.n26.service.StatisticsService;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.sql.Timestamp;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class StatisticsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StatisticsService statisticsService;

    private final ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private StatisticsController statisticsController;
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(statisticsController).build();
    }

    @SneakyThrows
    @Test
    public void testTransaction() {
        doNothing().when(statisticsService).addStatistics(Mockito.any(BigDecimal.class), Mockito.anyLong());

        TransactionRequest transaction = createTransaction(new BigDecimal("1.0"), new Timestamp(System.currentTimeMillis()) );

        mockMvc.perform(post("/transactions").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(transaction)))
                .andExpect(status().is(HttpServletResponse.SC_CREATED));
    }

    @SneakyThrows
    @Test
    public void testGetStatistics()  {
        StatisticResponse statistics = StatisticResponse.newBuilder().
                withCount(5).
                withSum(BigDecimal.valueOf(20.8)).
                withAvg(BigDecimal.valueOf(5.6)).
                withMin(BigDecimal.valueOf(3.4)).
                withMax(BigDecimal.valueOf(6.7)).
                build();
        Mockito.when(statisticsService.getStatistics()).thenReturn(statistics);

        mockMvc.perform(get("/statistics").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.count").value(statistics.getCount()))
                .andExpect(jsonPath("$.avg").value(statistics.getAvg()))
                .andExpect(jsonPath("$.sum").value(statistics.getSum()))
                .andExpect(jsonPath("$.min").value(statistics.getMin()))
                .andExpect(jsonPath("$.max").value(statistics.getMax()));
    }

    @SneakyThrows
    @Test
    public  void deleteTransaction(){
        doNothing().when(statisticsService).deleteTransitions();
        mockMvc.perform(delete("/transactions").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful());


    }

    private TransactionRequest createTransaction(BigDecimal amount, Timestamp timestamp) {
        TransactionRequest transaction = new TransactionRequest();
        transaction.setAmount(amount);
        transaction.setTimestamp(timestamp);
        return transaction;
    }


}