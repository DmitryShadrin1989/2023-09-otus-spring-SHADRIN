package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Order;
import ru.otus.hw.domain.Ware;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class MarketplaceOrdersServiceImpl implements MarketplaceOrdersService {

    private final MarketplaceGateway marketplace;

    private final WarehouseService warehouseService;

    @Override
    public void start() {
        Ware ware1 = new Ware(1L, "T-shirt with a \"Smile\" print");
        Ware ware2 = new Ware(2L, "Space Print T-shirt");
        Ware ware3 = new Ware(3L, "Coffee mug with \"Big Boss\" print");
        Ware ware4 = new Ware(4L, "Mouse pad with \"Night City\" print");
        Ware ware5 = new Ware(5L, "Calendar 2024 \"Funny pets\"");

        warehouseService.putGoodsInWarehouse(Map.of(ware1, 100, ware2, 100, ware3, 100, ware4, 100,
                ware5, 100));

        Order order1 = new Order(1L, "Carl Edward Sagan", Map.of(ware1, 50, ware2, 50,
                ware3, 50, ware4, 50, ware5, 50));
        Order order2 = new Order(2L, "Neil deGrasse Tyson", Map.of(ware1, 50, ware2, 50,
                ware3, 50, ware4, 50, ware5, 50));
        Order order3 = new Order(3L, "Vladimir Georgievich Surdin", Map.of(ware1, 10, ware2, 10,
                ware3, 10, ware4, 10, ware5, 10));

        log.info("We are putting orders into operation");
        marketplace.process(order1);
        marketplace.process(order2);
        marketplace.process(order3);
    }
}
