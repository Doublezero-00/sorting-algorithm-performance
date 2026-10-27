package lk.ac.uoc.sortapp.data;

import java.util.Collections;
import java.util.List;

/**
 * Holds the contents of a loaded CSV file: column headers plus raw string rows.
 * Shared data model between Member 1 (CsvLoader) and Member 3 (GUI/benchmark).
 * Agreed on day one so every branch compiles against the same type.
 */
public class Dataset {

    private final List<String> headers;
    private final List<String[]> rows;

    public Dataset(List<String> headers, List<String[]> rows) {
        this.headers = Collections.unmodifiableList(headers);
        this.rows = Collections.unmodifiableList(rows);
    }

    public List<String> getHeaders() {
        return headers;
    }

    public List<String[]> getRows() {
        return rows;
    }

    public int getColumnCount() {
        return headers.size();
    }

    public int getRowCount() {
        return rows.size();
    }
}
