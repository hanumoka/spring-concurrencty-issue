package hanu.exam.stock.service;


import hanu.exam.stock.domain.Stock;
import hanu.exam.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@Service
public class StockService {
    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Transactional
    public void decrease(Long id, Long quantity){
        // 동시성 문제가 발생하는 코드

        // stock을 가져온다.
        // 재고를 감소기킨다.
        // 저장
        Stock stock = stockRepository.findById(id).orElseThrow();
        stock.decrease(quantity);
        stockRepository.saveAndFlush(stock); // saveAndFLush 할 필요가 있는가?
    }



}
