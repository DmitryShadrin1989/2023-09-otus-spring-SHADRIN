package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.config.JobConfig;
import ru.otus.hw.converters.DataSourceConverter;
import ru.otus.hw.services.DataSourceRepository;

import java.util.Properties;

@ShellComponent
@RequiredArgsConstructor
public class JobCommands {

    private final DataSourceRepository dataSourceRepository;

    private final DataSourceConverter dataSourceConverter;

    private final JobOperator jobOperator;

    @ShellMethod(value = "Start data migration", key = "sdm")
    public String startDataMigrationJob() throws Exception {
        Properties jobParameters = new Properties();
        jobParameters.put("Time", String.valueOf(System.currentTimeMillis()));
        jobOperator.start(JobConfig.JOB_NAME, jobParameters);
        return "The migration has been completed";
    }

    @ShellMethod(value = "Find all the rows of the table", key = "art")
    public String findAllRows(String table) {
        return dataSourceConverter.dataSetToString(dataSourceRepository.getTableData(table));
    }

    @ShellMethod(value = "Find all the source tables", key = "ast")
    public String findAllRows() {
        return dataSourceConverter.dataSetToString(dataSourceRepository.getAllTables());
    }
}
