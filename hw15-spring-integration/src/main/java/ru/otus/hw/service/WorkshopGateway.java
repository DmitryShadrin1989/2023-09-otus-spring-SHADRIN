package ru.otus.hw.service;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.hw.domain.Ware;

import java.util.Map;

@MessagingGateway
public interface WorkshopGateway {

    @Gateway(requestChannel = "ordersForProductionOfGoodsChannel")
    void process(Map<Ware, Integer> wares);
}
