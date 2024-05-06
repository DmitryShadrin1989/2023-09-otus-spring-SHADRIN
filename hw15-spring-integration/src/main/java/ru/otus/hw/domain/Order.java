package ru.otus.hw.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class Order {

    private Long id;

    private String customer;

    private Map<Ware, Integer> wares;
}
