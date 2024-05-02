package ru.otus.hw.service;

import ru.otus.hw.domain.Order;
import ru.otus.hw.domain.Ware;

import java.util.Map;

public interface WarehouseService {

    Order processOrder(Order order);

    int getStockOfGoods(Ware ware);

    boolean takeGoodsFromWarehouse(Ware ware, int quantity);

    void putGoodsInWarehouse(Map<Ware, Integer> wares);
}


