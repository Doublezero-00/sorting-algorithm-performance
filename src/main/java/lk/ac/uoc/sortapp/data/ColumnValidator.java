package lk.ac.uoc.sortapp.data;


public class ColumnValidator {

    /** @return true if the column can be used for sorting. */
    public boolean isNumeric(Dataset dataset, int columnIndex) {
        if (dataset == null || columnIndex < 0 || columnIndex >= dataset.getColumnCount()) {
            return false;
        }

        int numberCount = 0;
        for (String[] row : dataset.getRows()) {
            String cell = row[columnIndex];
            if (cell.isEmpty()) {
                continue;                   // ignore blank cells
            }
            if (!isNumber(cell)) {
                return false;               // found text -> not a numeric column
            }
            numberCount++;
        }
        return numberCount > 0;             // all-blank column is not usable
    }

    /**
     * Copies the chosen column into a double array (blank cells are skipped).
     *
     * @throws IllegalArgumentException with a friendly message if the column is invalid.
     */
    public double[] toDoubleArray(Dataset dataset, int columnIndex) {
        if (dataset == null || columnIndex < 0 || columnIndex >= dataset.getColumnCount()) {
            throw new IllegalArgumentException("Invalid column selected.");
        }

        String columnName = dataset.getHeaders().get(columnIndex);
        double[] values = new double[dataset.getRowCount()];   // big enough for every row
        int count = 0;

        for (int i = 0; i < dataset.getRowCount(); i++) {
            String cell = dataset.getRows().get(i)[columnIndex];

            if (cell.isEmpty()) {
                continue;                   // skip blank cell
            }
            if (!isNumber(cell)) {
                throw new IllegalArgumentException("Column '" + columnName
                        + "' contains a non-numeric value '" + cell
                        + "' (data row " + (i + 1) + "). Please select a numeric column.");
            }
            values[count] = Double.parseDouble(cell);
            count++;
        }

        if (count == 0) {
            throw new IllegalArgumentException("Column '" + columnName + "' is empty.");
        }

        // Return an array with exactly 'count' elements (no unused slots at the end)
        double[] result = new double[count];
        System.arraycopy(values, 0, result, 0, count);
        return result;
    }

    /** @return true if the text is a normal number such as 12, -3.5 or 1e3. */
    private boolean isNumber(String text) {
        // Reject things Java would accept by surprise: "NaN", "Infinity", "12f", "7d"
        char last = text.charAt(text.length() - 1);
        if (!Character.isDigit(last) && last != '.') {
            return false;
        }
        try {
            double value = Double.parseDouble(text);
            return !Double.isNaN(value) && !Double.isInfinite(value);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}