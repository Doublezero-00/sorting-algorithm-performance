package lk.ac.uoc.sortapp.ui;

import lk.ac.uoc.sortapp.benchmark.BenchmarkResult;
import lk.ac.uoc.sortapp.benchmark.BenchmarkRunner;
import lk.ac.uoc.sortapp.data.ColumnValidator;
import lk.ac.uoc.sortapp.data.CsvLoader;
import lk.ac.uoc.sortapp.data.Dataset;
import lk.ac.uoc.sortapp.sorting.HeapSort;
import lk.ac.uoc.sortapp.sorting.InsertionSort;
import lk.ac.uoc.sortapp.sorting.MergeSort;
import lk.ac.uoc.sortapp.sorting.QuickSort;
import lk.ac.uoc.sortapp.sorting.ShellSort;
import lk.ac.uoc.sortapp.sorting.SortingAlgorithm;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MainFrame extends JFrame {

    // ---- Helper objects from the other members' packages ----
    private final CsvLoader csvLoader = new CsvLoader();                    // Member 1
    private final ColumnValidator columnValidator = new ColumnValidator();  // Member 1
    private final BenchmarkRunner benchmarkRunner = new BenchmarkRunner();  // Member 3

    // The five algorithms written by Member 2
    private final List<SortingAlgorithm> algorithms = Arrays.asList(
            new InsertionSort(),
            new ShellSort(),
            new MergeSort(),
            new QuickSort(),
            new HeapSort());

    // The loaded file's data (stays null until a valid file is opened)
    private Dataset dataset = null;

    // ---- GUI components ----
    private final JButton chooseButton = new JButton("Choose CSV File...");
    private final JLabel fileLabel = new JLabel("No file selected");
    private final JComboBox<String> columnBox = new JComboBox<>();
    private final JButton sortButton = new JButton("Sort and Compare");

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Algorithm", "Execution Time (ms)", "Result"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;                        // user must not edit the results
        }
    };
    private final JTable resultTable = new JTable(tableModel);

    private final JLabel bestLabel = new JLabel("Best algorithm: -");
    private final JTextArea previewArea = new JTextArea(3, 40);
    private final JLabel statusLabel = new JLabel("Please choose a CSV file to begin.");

    public MainFrame() {
        super("Sorting Algorithm Performance Evaluation");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);             // open in the middle of the screen
        setLayout(new BorderLayout(10, 10));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        buildTopPanel();
        buildCenterPanel();
        buildBottomPanel();

        // Buttons do nothing until a file is loaded
        columnBox.setEnabled(false);
        sortButton.setEnabled(false);

        // What happens when a button is clicked
        chooseButton.addActionListener(e -> chooseFile());
        sortButton.addActionListener(e -> runSort());
    }

    // =====================================================================
    //  Building the window
    // =====================================================================

    private void buildTopPanel() {
        JPanel fileRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fileRow.add(chooseButton);
        fileRow.add(fileLabel);

        JPanel columnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        columnRow.add(new JLabel("Select column to sort:"));
        columnRow.add(columnBox);
        columnRow.add(sortButton);

        JPanel top = new JPanel(new BorderLayout());
        top.add(fileRow, BorderLayout.NORTH);
        top.add(columnRow, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);
    }

    private void buildCenterPanel() {
        resultTable.setRowHeight(24);
        JScrollPane scroll = new JScrollPane(resultTable);
        scroll.setBorder(BorderFactory.createTitledBorder("Performance of each sorting algorithm"));
        add(scroll, BorderLayout.CENTER);
    }

    private void buildBottomPanel() {
        bestLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        bestLabel.setForeground(new Color(0, 120, 0));

        previewArea.setEditable(false);
        JScrollPane previewScroll = new JScrollPane(previewArea);
        previewScroll.setBorder(BorderFactory.createTitledBorder("Sorted data preview"));

        JPanel bottom = new JPanel(new BorderLayout(5, 5));
        bottom.add(bestLabel, BorderLayout.NORTH);
        bottom.add(previewScroll, BorderLayout.CENTER);
        bottom.add(statusLabel, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);
    }

    // =====================================================================
    //  Step 1: choosing and loading the CSV file
    // =====================================================================

    private void chooseFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select a CSV file");
        chooser.setFileFilter(new FileNameExtensionFilter("CSV files (*.csv)", "csv"));

        int choice = chooser.showOpenDialog(this);
        if (choice != JFileChooser.APPROVE_OPTION) {
            return;                              // user pressed Cancel
        }

        File file = chooser.getSelectedFile();
        try {
            dataset = csvLoader.load(file);      // Member 1's code

            fileLabel.setText(file.getName() + "   (" + dataset.getRowCount()
                    + " rows, " + dataset.getColumnCount() + " columns)");
            fillColumnBox();
            clearResults();

            columnBox.setEnabled(true);
            sortButton.setEnabled(true);
            statusLabel.setText("File loaded. Select a numeric column and click 'Sort and Compare'.");

        } catch (IOException e) {
            // Invalid file, empty file, unreadable file ... show a friendly message
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Cannot open file", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Puts every column name in the drop-down, marked as numeric or not. */
    private void fillColumnBox() {
        columnBox.removeAllItems();
        int firstNumeric = -1;

        for (int i = 0; i < dataset.getColumnCount(); i++) {
            boolean numeric = columnValidator.isNumeric(dataset, i);
            String label = dataset.getHeaders().get(i) + (numeric ? "  (numeric)" : "  (not numeric)");
            columnBox.addItem(label);

            if (numeric && firstNumeric == -1) {
                firstNumeric = i;
            }
        }
        if (firstNumeric != -1) {
            columnBox.setSelectedIndex(firstNumeric);   // pre-select the first numeric column
        }
    }

    // =====================================================================
    //  Step 2: sorting and measuring
    // =====================================================================

    private void runSort() {
        if (dataset == null) {
            showWarning("Please choose a CSV file first.");
            return;
        }

        final double[] values = readSelectedColumn();
        if (values == null) {
            return;                              // a message was already shown
        }

        setBusy(true);
        statusLabel.setText("Sorting " + values.length + " values with 5 algorithms... please wait.");

        // SwingWorker runs the sorting in the BACKGROUND so the window does not freeze
        // (Insertion Sort can take several seconds on big files).
        SwingWorker<List<BenchmarkResult>, Void> worker = new SwingWorker<List<BenchmarkResult>, Void>() {

            @Override
            protected List<BenchmarkResult> doInBackground() {
                return benchmarkRunner.run(values, algorithms);     // runs in the background
            }

            @Override
            protected void done() {
                // runs again on the GUI thread when the background work has finished
                try {
                    showResults(get(), values.length);
                } catch (InterruptedException e) {
                    statusLabel.setText("Sorting was interrupted.");
                } catch (ExecutionException e) {
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Sorting failed: " + e.getCause(), "Error", JOptionPane.ERROR_MESSAGE);
                    statusLabel.setText("Sorting failed.");
                } finally {
                    setBusy(false);
                }
            }
        };
        worker.execute();
    }

    /** Gets the chosen column as numbers, or shows a message and returns null if it is not usable. */
    private double[] readSelectedColumn() {
        int columnIndex = columnBox.getSelectedIndex();
        if (columnIndex < 0) {
            showWarning("Please select a column.");
            return null;
        }
        try {
            return columnValidator.toDoubleArray(dataset, columnIndex);   // Member 1's code
        } catch (IllegalArgumentException e) {
            showWarning(e.getMessage());         // e.g. "column contains text" or "column is empty"
            return null;
        }
    }

    // =====================================================================
    //  Step 3: showing the results
    // =====================================================================

    private void showResults(List<BenchmarkResult> results, int valueCount) {
        BenchmarkResult best = benchmarkRunner.findBest(results);

        tableModel.setRowCount(0);               // clear old rows
        for (BenchmarkResult result : results) {
            String mark = (result == best) ? "BEST" : "";
            tableModel.addRow(new Object[]{
                    result.getAlgorithmName(),
                    String.format("%.3f", result.getMillis()),
                    mark});
        }

        bestLabel.setText("Best algorithm: " + best.getAlgorithmName()
                + "   (" + String.format("%.3f", best.getMillis()) + " ms)");
        previewArea.setText(buildPreview(best.getSorted()));
        statusLabel.setText("Done. Sorted " + valueCount + " values with all 5 algorithms.");
    }

    /** Shows the smallest and largest few values so the user can see the data really is sorted. */
    private String buildPreview(double[] sorted) {
        int show = Math.min(10, sorted.length);
        StringBuilder text = new StringBuilder();

        text.append("Smallest ").append(show).append(" values: ");
        for (int i = 0; i < show; i++) {
            if (i > 0) {
                text.append(", ");
            }
            text.append(formatNumber(sorted[i]));
        }

        text.append("\nLargest ").append(show).append(" values: ");
        for (int i = sorted.length - show; i < sorted.length; i++) {
            if (i > sorted.length - show) {
                text.append(", ");
            }
            text.append(formatNumber(sorted[i]));
        }
        return text.toString();
    }

    /** 21.0 -> "21", 3.45 -> "3.45" */
    private String formatNumber(double value) {
        if (value == Math.rint(value) && Math.abs(value) < 1e15) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }

    // =====================================================================
    //  Small helpers
    // =====================================================================

    private void clearResults() {
        tableModel.setRowCount(0);
        bestLabel.setText("Best algorithm: -");
        previewArea.setText("");
    }

    /** Disables the controls while sorting is running. */
    private void setBusy(boolean busy) {
        chooseButton.setEnabled(!busy);
        sortButton.setEnabled(!busy && dataset != null);
        columnBox.setEnabled(!busy && dataset != null);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }
}