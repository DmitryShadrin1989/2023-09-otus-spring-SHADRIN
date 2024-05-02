package ru.otus.hw.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Ware;
import ru.otus.hw.util.ServiceUtil;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WorkshopServiceImpl implements WorkshopService {

    static final double STOCK_RATIO = 1.5;

    @Override
    public Map<Ware, Integer> toProduceGoods(Map<Ware, Integer> wares) {
        Map<Ware, Integer> manufacturedGoods = new HashMap<>();
        for (Map.Entry<Ware, Integer> entry : wares.entrySet()) {
            ServiceUtil.delay(2000);
            Ware ware = entry.getKey();
            int orderedQuantity = entry.getValue();
            int producedWithStock = (int) Math.round(orderedQuantity * STOCK_RATIO);
            manufacturedGoods.put(ware, producedWithStock);
            log.info("{} product was produced in the amount of {}", ware.getName(), producedWithStock);
        }
        log.info("The manufactured goods have been sent to the warehouse");
        return manufacturedGoods;
    }
}
