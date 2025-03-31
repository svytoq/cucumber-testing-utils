package ru.itmo.integration.jpa.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import java.util.List;
import lombok.RequiredArgsConstructor;
import ru.itmo.integration.jpa.repositories.TestRepository;


@RequiredArgsConstructor
public class JdbcSteps {

  private static final String RESET_SEQ = "alter sequence %s restart with %d";

  private final TestRepository repository;

  @Given("Db table {string} is empty")
  public void dbTableIsEmpty(String tableName) {
    repository.execute("truncate " + tableName + " cascade");
  }

  @And("Db table {string} contains data:")
  public void dbTableContainsData(String tableName, DataTable dataTable) {
    List<List<String>> lists = dataTable.asLists();
    repository.insertDictionaryTableRows(tableName, lists);
  }

  @And("Db sequence {string} reset to {int}")
  public void dbSequenceResetTo(String seqName, int value) {
    repository.execute(RESET_SEQ.formatted(seqName, value));
  }

}