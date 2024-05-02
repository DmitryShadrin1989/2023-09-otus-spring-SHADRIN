package ru.otus.hw.services;

import java.util.List;

public interface DataSourceRepository {

    List<String> getTableData(String table);

    List<String> getAllTables();
}
