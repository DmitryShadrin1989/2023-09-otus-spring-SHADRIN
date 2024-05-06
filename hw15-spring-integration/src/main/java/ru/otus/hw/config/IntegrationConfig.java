package ru.otus.hw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannelSpec;
import org.springframework.integration.dsl.MessageChannels;
import ru.otus.hw.service.PickUpPointForOrdersService;
import ru.otus.hw.service.WarehouseService;
import ru.otus.hw.service.WorkshopService;

@Configuration
public class IntegrationConfig {

    @Bean
    public MessageChannelSpec<?, ?> incomingOrdersChannel() {
        return MessageChannels.queue(10);
    }

    @Bean
    public MessageChannelSpec<?, ?> ordersToBeIssuedChannel() {
        return MessageChannels.publishSubscribe();
    }

    @Bean
    public MessageChannelSpec<?, ?> ordersForProductionOfGoodsChannel() {
        return MessageChannels.publishSubscribe();
    }

    @Bean
    public IntegrationFlow marketplaceOrdersFlow(WarehouseService warehouseService) {
        return IntegrationFlow.from(incomingOrdersChannel())
                .handle(warehouseService, "processOrder")
                .channel(ordersToBeIssuedChannel())
                .get();
    }

    @Bean
    public IntegrationFlow workshopFlow(WorkshopService workshopService,
                                        WarehouseService warehouseService) {
        return IntegrationFlow.from(ordersForProductionOfGoodsChannel())
                .handle(workshopService, "toProduceGoods")
                .handle(warehouseService, "putGoodsInWarehouse")
                .get();
    }

    @Bean
    public IntegrationFlow pickUpPointForOrdersFlow(PickUpPointForOrdersService pickUpPointForOrdersService) {
        return IntegrationFlow.from(ordersToBeIssuedChannel())
                .handle(pickUpPointForOrdersService, "issueOrderToCustomer")
                .get();
    }
}
