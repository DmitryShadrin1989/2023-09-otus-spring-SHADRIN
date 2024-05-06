package ru.otus.hw.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Order;
import ru.otus.hw.domain.Ware;
import ru.otus.hw.util.ServiceUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class WarehouseServiceImpl implements WarehouseService {

    private final WorkshopGateway workshopGateway;

    private final Map<Ware, Integer> remainingGoodsInWarehouse;

    public WarehouseServiceImpl(WorkshopGateway workshopGateway) {
        this.workshopGateway = workshopGateway;
        this.remainingGoodsInWarehouse = new ConcurrentHashMap<>();
    }

    @Override
    public Order processOrder(Order order) {
        log.info("We process the order N {}", order.getId());
        ServiceUtil.delay(3000);

        Map<Ware, Integer> orderedProducts = order.getWares();
        Map<Ware, Integer> orderingGoodsForProduction = new HashMap<>();
        for (Map.Entry<Ware, Integer> entry : orderedProducts.entrySet()) {
            Ware ware = entry.getKey();
            int orderedQuantity = entry.getValue();
            boolean isEnough = takeGoodsFromWarehouse(ware, orderedQuantity);
            if (!isEnough) {
                int toProduceQuantity = orderedQuantity - getStockOfGoods(ware);
                orderingGoodsForProduction.put(ware, toProduceQuantity);

                log.info("A production order for the {} product in the amount of {} will be created in the workshop",
                        ware.getName(), toProduceQuantity);
            }
        }
        if (!orderingGoodsForProduction.isEmpty()) {
            workshopGateway.process(orderingGoodsForProduction);
            takeProducedGoods(orderingGoodsForProduction);
        }
        return order;
    }

    public void takeProducedGoods(Map<Ware, Integer> goodsInProduction) {
        boolean allGoodsEnough = false;
        while (!allGoodsEnough) {
            log.info("We will try to get the goods from the warehouse");
            ServiceUtil.delay(8000);

            allGoodsEnough = true;
            for (Map.Entry<Ware, Integer> entry : goodsInProduction.entrySet()) {
                Ware ware = entry.getKey();
                int orderedQuantity = entry.getValue();
                if (orderedQuantity == 0) {
                    continue;
                }
                boolean isEnough = takeGoodsFromWarehouse(ware, orderedQuantity);
                if (isEnough) {
                    goodsInProduction.put(ware, 0);
                }
                allGoodsEnough = isEnough && allGoodsEnough;
            }
            if (!allGoodsEnough) {
                log.info("Not all products were received from the warehouse, " +
                        "let's wait until they are produced and arrive at the warehouse");
            }
        }
    }

    @Override
    public int getStockOfGoods(Ware ware) {
        return remainingGoodsInWarehouse.getOrDefault(ware, 0);
    }

    @Override
    public boolean takeGoodsFromWarehouse(Ware ware, int quantity) {
        ServiceUtil.delay(1000);

        int stockOfGoods = getStockOfGoods(ware);
        if (stockOfGoods < quantity) {
            log.info("It was not possible to take goods {} in quantity {} from the warehouse, " +
                    "because the rest of the goods {}", ware.getName(), quantity, stockOfGoods);
            return false;
        }
        remainingGoodsInWarehouse.put(ware, stockOfGoods - quantity);

        log.info("The {} product was successfully taken from the warehouse in the amount of {}",
                ware.getName(), quantity);
        return true;
    }

    @Override
    public void putGoodsInWarehouse(Map<Ware, Integer> wares) {
        for (Map.Entry<Ware, Integer> entry : wares.entrySet()) {
            ServiceUtil.delay(1000);

            Ware ware = entry.getKey();
            int quantity = entry.getValue();
            int stockOfGoods = getStockOfGoods(ware);
            int newStockOfGoods = stockOfGoods + quantity;
            remainingGoodsInWarehouse.put(ware, newStockOfGoods);

            log.info("The warehouse received {} goods in the amount of {}. The remainder is {}",
                    ware.getName(), quantity, newStockOfGoods);
        }
    }
}
