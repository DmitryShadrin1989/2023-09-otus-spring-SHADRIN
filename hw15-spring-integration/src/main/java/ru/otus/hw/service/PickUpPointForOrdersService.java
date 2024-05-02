package ru.otus.hw.service;

import ru.otus.hw.domain.Order;

public interface PickUpPointForOrdersService {

    void issueOrderToCustomer(Order order);
}
