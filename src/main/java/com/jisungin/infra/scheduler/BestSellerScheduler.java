package com.jisungin.infra.scheduler;

import com.jisungin.application.book.BestSellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BestSellerScheduler {

    private final BestSellerService bestSellerService;

    @Async
    @Scheduled(cron = "0 0 4 * * ?")
    public void updateBestSellers() {
        bestSellerService.updateBestSellers();
    }

}