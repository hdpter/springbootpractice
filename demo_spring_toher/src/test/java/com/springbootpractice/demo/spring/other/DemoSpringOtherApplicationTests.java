package com.springbootpractice.demo.spring.other;

import com.springbootpractice.demo.spring.other.service.AsyncSummaryDataService;
import com.springbootpractice.demo.spring.other.service.SummaryDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class DemoSpringOtherApplicationTests {

    @Autowired
    private SummaryDataService summaryDataService;

    @Autowired
    private AsyncSummaryDataService asyncSummaryDataService;

    @Test
    void asyncTest() {

        asyncSummaryDataService.calculateDayActiveData(LocalDate.now());
        asyncSummaryDataService.calculateDayLeftData(LocalDate.now());
        asyncSummaryDataService.calculateDayNewData(LocalDate.now());

    }

    @Test
    void syncTest() {

        summaryDataService.calculateDayActiveData(LocalDate.now());
        summaryDataService.calculateDayLeftData(LocalDate.now());
        summaryDataService.calculateDayNewData(LocalDate.now());

    }

}
