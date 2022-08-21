package hanu.exam.stock.service;

import hanu.exam.stock.domain.Stock;
import hanu.exam.stock.repository.StockRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockServiceTest {

    @Autowired
    StockService stockService;

    @Autowired
    StockRepository stockRepository;

    @BeforeEach
    public void before(){
        Stock stock = new Stock(1L, 100L);
        stockRepository.saveAndFlush(stock);
    }

    @AfterEach
    public void after(){
        stockRepository.deleteAll();
    }

    @Test
    public void stock_decrease(){
        stockService.decrease(1L, 1L);

        Stock stock = stockRepository.findById(1L).orElseThrow();

        Assertions.assertThat(stock.getQuantity()).isEqualTo(99L);

    }

    @Test
    public void 동시에100개의요청() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for(int i =0; i < threadCount; i++){
            executorService.submit(() -> {
                try{
                    stockService.decrease(1l, 1l);
                }finally {
                    latch.countDown();
                }
            });
        }//for

        latch.await();

        Stock stock = stockRepository.findById(1l).orElseThrow();

        //예상은 100 - ( 1 * 100) => 0이라 예상이 되지만 동시성 오류에 의해 오류가 발생한다. 레이스 컨디션이 발생한다?
        Assertions.assertThat(stock.getQuantity()).isEqualTo(0);

    }

}