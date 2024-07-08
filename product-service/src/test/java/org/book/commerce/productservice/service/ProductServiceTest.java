package org.book.commerce.productservice.service;

//import org.book.commerce.productservice.domain.Product;
//import org.book.commerce.productservice.dto.OrderProductCountFeignRequest;
//import org.book.commerce.productservice.repository.ProductRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//// 락을 걸지않고 테스트했을때에는 다 끝난 후 재고가 1만 감소하였음. Race Condition 발생
//@SpringBootTest
//class ProductServiceTest {
//
//    @Autowired
//    private ProductRepository productRepository;
//
//    @Autowired
//    private ProductService productService;
//
//    @Test
//    @DisplayName("재고보다 많은 주문이 동시에 들어올 경우, 늦게 들어온 주문은 예외를 던진다")
//    void minusStock() throws InterruptedException {
//        //given
//        int numThreads = 20;
//        Long productId = 1L;
//        OrderProductCountFeignRequest orderProduct = new OrderProductCountFeignRequest(productId,1);
//
//        AtomicInteger successCount = new AtomicInteger();
//        AtomicInteger failCount = new AtomicInteger();
//
//        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
//        CountDownLatch latch = new CountDownLatch(numThreads);
//
//        for(int i=0;i<numThreads;i++){
//            executorService.submit(()->{
//                try{
//                    productService.minusStock(String.valueOf(2L),orderProduct);
//                    successCount.incrementAndGet();
//                } catch (Exception e){
//                    System.out.println(e.getMessage());
//                    failCount.incrementAndGet();
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//        latch.await();
//        executorService.shutdown();
//
//
//        System.out.println("successCount = "+successCount);
//        System.out.println("failCount = "+failCount);
//    }
//}