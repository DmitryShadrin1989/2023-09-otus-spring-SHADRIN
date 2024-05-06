package ru.otus.hw.service;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.hw.domain.Order;

@MessagingGateway
public interface MarketplaceGateway {

    @Gateway(requestChannel = "incomingOrdersChannel", replyChannel = "ordersToBeIssuedChannel")
    Order process(Order order);
}
