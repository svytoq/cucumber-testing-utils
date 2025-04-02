package ru.itmo.integration.jpa.repositories;

import jakarta.transaction.Transactional;
import java.util.List;
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
    if (tableName == null || tableName.isBlank() || data == null || data.isEmpty()) {
      return false;
    }

    if (!tableExists(tableName)) {
      return false;
    }

    String idType = jdbc.queryForObject(
            "SELECT data_type FROM information_schema.columns " +
                    "WHERE table_name = LOWER(?) AND column_name = 'id'",
            String.class,
            tableName
    );

    if (!"bigint".equalsIgnoreCase(idType)) {
      throw new IllegalStateException("Тип поля id должен быть bigint");
    }

    for (List<String> row : data) {
      if (row == null || row.isEmpty()) continue;

      try {
        String query = "SELECT EXISTS(SELECT 1 FROM " + tableName +
                " WHERE id = (SELECT (?::text)::bigint LIMIT 1)";


        Boolean exists = jdbc.queryForObject(
                query,
                Boolean.class,
                row.get(0)
        );

        if (exists == null || !exists) {
          return false;
        }
      } catch (DataAccessException e) {
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