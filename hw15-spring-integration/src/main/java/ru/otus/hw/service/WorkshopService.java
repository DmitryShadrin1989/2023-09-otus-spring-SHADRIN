package ru.otus.hw.service;

import ru.otus.hw.domain.Ware;

import java.util.Map;

public interface WorkshopService {

    Map<Ware, Integer> toProduceGoods(Map<Ware, Integer> wares);
}
