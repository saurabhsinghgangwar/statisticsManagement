package com.n26;

import com.n26.service.StatisticsService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {
    @Autowired
    private StatisticsService statisticsService;

    @Test
    public void contextLoads() {

        Assert.assertNotNull("statisticsService can not be null", statisticsService);
    }

}


