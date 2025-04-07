package ru.itmo.integration.jpa.repositories;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TestRepository {

  private static final String INSERT_FORMAT = "insert into %s (%s) values ";

  private final JdbcTemplate jdbc;

  @Transactional
  public void insertDictionaryTableRows(String tableName, List<List<String>> data) {
    String insertInto = compileInsertInto(tableName, data.get(0));

    for (int i = 1; i < data.size(); i++) {
      String values = compileValues(data.get(i));
      String sql = insertInto + values;
      jdbc.update(sql);
    }
  }

  public boolean checkDictionaryTableRows(String tableName, List<List<String>> data) {
    if (data.isEmpty()) {
      return false;
    }

    List<String> columnNames = data.get(0);

    for (int i = 1; i < data.size(); i++) {
      List<String> row = data.get(i);

      long id = Long.parseLong(row.get(0));
      String sql = "SELECT * FROM " + tableName + " WHERE id = ?";

      List<Map<String, Object>> dbRow = jdbc.queryForList(sql, id);

      if (dbRow.isEmpty()) {
        return false;
      }

      Map<String, Object> dbRowData = dbRow.get(0);

      for (int j = 0; j < columnNames.size(); j++) {
        String columnName = columnNames.get(j);
        String inputValue = row.get(j);

        Object dbValue = dbRowData.get(columnName);

        if (dbValue == null) {
          if (inputValue != null) {
            return false;
          }
        } else {
          if (!dbValue.toString().equals(inputValue)) {
            return false;
          }
        }
      }
    }

    return true;
  }
  @Transactional
  public void execute(String sql) {
    jdbc.execute(sql);
  }

  private String compileInsertInto(String tableName, List<String> header) {
    String fields = String.join(", ", header);
    return INSERT_FORMAT.formatted(tableName, fields);
  }

  private String compileValues(List<String> values) {
    return values.stream()
        .map(v -> v != null ? "'" + v + "'" : "null")
        .collect(Collectors.joining(", ", "(", ")"));
  }

  private boolean tableExists(String tableName) {
    String query = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = ?";
    Integer count = jdbc.queryForObject(query, Integer.class, tableName.toLowerCase());
    return count != null && count > 0;
  }
}