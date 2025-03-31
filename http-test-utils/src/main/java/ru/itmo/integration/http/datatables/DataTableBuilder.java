package ru.itmo.integration.http.datatables;

import io.cucumber.datatable.DataTable;
import java.util.ArrayList;
import java.util.List;

public abstract class DataTableBuilder<T> {

  public DataTable buildForList(List<T> source) {
    List<List<String>> data = new ArrayList<>();

    data.add(dataTableHeader());
    for (T item : source) {
      data.add(dataTableData(item));
    }

    return DataTable.create(data);
  }

  public DataTable build(T source) {
    return buildForList(List.of(source));
  }

  protected abstract List<String> dataTableHeader();

  protected abstract List<String> dataTableData(T item);

}