package com.jisungin.infra.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!default")
@Component
@RequiredArgsConstructor
public class BestSellerTaskExecutor implements CommandLineRunner {

    private final BestSellerScheduler bestSellerScheduler;

    @Override
    public void run(String... args) throws Exception {
        bestSellerScheduler.updateBestSellers();
    }

}
