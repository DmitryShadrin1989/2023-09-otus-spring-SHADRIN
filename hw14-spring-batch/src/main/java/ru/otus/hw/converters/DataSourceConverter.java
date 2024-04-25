package ru.otus.hw.converters;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSourceConverter {
    public String dataSetToString(List<String> dataSet) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String data : dataSet) {
            stringBuilder.append(data).append("\n");
        }
        return stringBuilder.toString();
    }
}
