package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DataSourceRepositoryImpl implements DataSourceRepository {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public List<String> getTableData(String table) {
        return namedParameterJdbcOperations.query("select * from  %s".formatted(table), new DataSourceRowMapper());
    }

    @Override
    public List<String> getAllTables() {
        return namedParameterJdbcOperations.query("show tables", new DataSourceRowMapper());
    }

    private static class DataSourceRowMapper implements RowMapper<String> {

        @Override
        public String mapRow(ResultSet rs, int i) throws SQLException {
            StringBuilder stringBuilder = new StringBuilder();
            for (int j = 1; j <= rs.getMetaData().getColumnCount(); j++) {
                stringBuilder.append(rs.getMetaData().getColumnName(j))
                        .append(": ").append(rs.getString(j)).append("; ");
            }
            return stringBuilder.toString();
        }
    }
}
