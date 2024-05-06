package ru.otus.hw.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Order;
import ru.otus.hw.util.ServiceUtil;

@Service
@Slf4j
public class PickUpPointForOrdersServiceImpl implements PickUpPointForOrdersService {

    @Override
    public void issueOrderToCustomer(Order order) {
        ServiceUtil.delay(2000);
        log.info("Order N {} has been issued to customer {}", order.getId(), order.getCustomer());
    }
}
