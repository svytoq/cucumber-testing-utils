package ru.itmo.integration.jpa.repositories;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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
    if (tableName == null || tableName.isBlank() || data == null || data.isEmpty()) {
      return false;
    }

    if (!tableExists(tableName)) {
      return false;
    }

    for (List<String> row : data) {
      try {
        String query = "SELECT COUNT(*) FROM " + tableName +
                " WHERE id = (CAST(:id AS TEXT)::BIGINT)";
        int count = jdbc.queryForObject(
                query,
                Integer.class,
                row.get(0)  // Передаем как строку, CAST преобразует в BIGINT
        );
        if (count == 0) return false;
      } catch (NumberFormatException e) {
        return false;
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