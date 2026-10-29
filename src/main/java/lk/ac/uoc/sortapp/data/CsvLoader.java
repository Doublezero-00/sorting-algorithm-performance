package lk.ac.uoc.sortapp.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class CsvLoader {

    public Dataset load(File file) throws IOException {

        // ---- Step 1: basic checks on the file itself ----
        if (file == null) {
            throw new IOException("No file was selected.");
        }
        if (!file.exists() || !file.isFile()) {
            throw new IOException("The selected file does not exist.");
        }
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            throw new IOException("Invalid file type. Please select a .csv file.");
        }
        if (file.length() == 0) {
            throw new IOException("The selected file is empty.");
        }

        List<String> headers = null;               // filled from the first line
        List<String[]> rows = new ArrayList<>();   // one String[] per data line

        // ---- Step 2: read the file line by line ----
        // "try (...)" closes the file automatically when we are done.
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {

                if (isFirstLine) {
                    line = removeBom(line);
                    isFirstLine = false;
                }

                // A quoted cell may contain a line break (e.g. a two-line address).
                // An ODD number of quote characters means the record is not finished
                // yet, so keep appending the next line until the quotes balance.
                while (countQuotes(line) % 2 != 0) {
                    String nextLine = reader.readLine();
                    if (nextLine == null) {
                        break;                     // file ended inside a quote
                    }
                    line = line + "\n" + nextLine;
                }

                if (line.trim().isEmpty()) {
                    continue;                      // skip blank lines
                }

                List<String> cells = parseLine(line);

                if (headers == null) {
                    headers = cleanHeaders(cells); // first line = headers
                } else {
                    rows.add(fitToSize(cells, headers.size()));
                }
            }
        } catch (IOException e) {
            throw new IOException("Could not read the file: " + e.getMessage());
        }

        // ---- Step 3: make sure we actually got something useful ----
        if (headers == null) {
            throw new IOException("The file contains no data.");
        }
        if (rows.isEmpty()) {
            throw new IOException("The file has a header row but no data rows.");
        }

        return new Dataset(headers, rows);
    }

    /**
     * Splits one CSV line into cells.
     * Example:  1,"Perera, Kamal",21   ->   [1] [Perera, Kamal] [21]
     * A comma inside double quotes does NOT split the cell.
     */
    private List<String> parseLine(String line) {
        List<String> cells = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (insideQuotes) {
                if (c == '"') {
                    // Two quotes in a row ("") mean one literal quote character
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        current.append('"');
                        i++;
                    } else {
                        insideQuotes = false;      // closing quote
                    }
                } else {
                    current.append(c);
                }
            } else {
                if (c == '"') {
                    insideQuotes = true;           // opening quote
                } else if (c == ',') {
                    cells.add(current.toString().trim());
                    current.setLength(0);          // start a new cell
                } else {
                    current.append(c);
                }
            }
        }
        cells.add(current.toString().trim());      // the last cell
        return cells;
    }

    /** Counts the double-quote characters in a line (a "" pair counts as 2, so parity still works). */
    private int countQuotes(String line) {
        int count = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '"') {
                count++;
            }
        }
        return count;
    }

    /** Some programs (e.g. Excel) put an invisible BOM character at the start of the file. */
    private String removeBom(String line) {
        if (!line.isEmpty() && line.charAt(0) == '\uFEFF') {
            return line.substring(1);
        }
        return line;
    }

    /** Gives a name like "Column 3" to any header cell that is empty. */
    private List<String> cleanHeaders(List<String> cells) {
        List<String> headers = new ArrayList<>();
        for (int i = 0; i < cells.size(); i++) {
            String name = cells.get(i).trim();
            if (name.isEmpty()) {
                name = "Column " + (i + 1);
            }
            headers.add(name);
        }
        return headers;
    }

    /**
     * Makes every row exactly as long as the header row:
     * short rows are padded with empty cells, extra cells are dropped.
     */
    private String[] fitToSize(List<String> cells, int size) {
        String[] row = new String[size];
        for (int i = 0; i < size; i++) {
            row[i] = (i < cells.size()) ? cells.get(i) : "";
        }
        return row;
    }
}