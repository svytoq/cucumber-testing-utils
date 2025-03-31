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

}