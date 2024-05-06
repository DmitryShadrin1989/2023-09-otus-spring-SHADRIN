package ru.otus.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import ru.otus.hw.service.MarketplaceOrdersService;

@SpringBootApplication
public class Hw15SpringIntegrationApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Hw15SpringIntegrationApplication.class, args);

        MarketplaceOrdersService marketplaceOrdersService = context.getBean(MarketplaceOrdersService.class);
        marketplaceOrdersService.start();
    }
}