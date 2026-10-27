# Beginner Guide - How This Application Works and How To Work On It

This guide explains the project in plain language. Read **Part 1 and Part 2 together as a group**,
then read your own member section in Part 3.

---

## Part 1 - What the application does

You run the program and a window opens. The user:

1. clicks **Choose CSV File...** and picks a `.csv` file
2. picks one **numeric column** from a drop-down list
3. clicks **Sort and Compare**
4. sees how long each of the 5 sorting algorithms took, and which one was **BEST** (fastest)

### What happens inside (data flow)

```
CSV file on disk
      |
      v
CsvLoader.load(file)            Member 1   reads the file  ->  Dataset (headers + rows of text)
      |
      v
ColumnValidator                 Member 1   checks the column is numeric -> double[] (numbers)
      |
      v
BenchmarkRunner.run(...)        Member 3   gives each algorithm its own copy of the numbers
      |                                    and times it with System.nanoTime()
      v
InsertionSort / ShellSort /     Member 2   each one sorts its copy (interface: SortingAlgorithm)
MergeSort / QuickSort / HeapSort
      |
      v
BenchmarkResult list  ->  MainFrame (GUI) shows the table + BEST label        Member 3
```

Each box is a separate **class** in a separate file. Classes talk to each other only through the
small shared types: `Dataset`, `SortingAlgorithm` and `BenchmarkResult`. That is why three people can
work at the same time without breaking each other's code.

---

## Part 2 - Java ideas used in this project

| Idea | Where you see it | Simple meaning |
|---|---|---|
| **Class** | every `.java` file | A blueprint that groups data and methods |
| **Interface** | `SortingAlgorithm` | A promise: "any class that implements me has `getName()` and `sort()`" |
| **implements** | `class QuickSort implements SortingAlgorithm` | This class keeps that promise |
| **Package** | `lk.ac.uoc.sortapp.sorting` | A folder that groups related classes |
| **Array** `double[]` | all sorting code | A fixed-size list of numbers |
| **List / ArrayList** | `Dataset`, `BenchmarkRunner` | A list that can grow |
| **File / BufferedReader** | `CsvLoader` | Reading a text file line by line |
| **Exception** (`try/catch`, `throw`) | `CsvLoader`, `MainFrame` | A way to report errors without crashing |
| **Recursion** | `MergeSort`, `QuickSort` | A method that calls itself on a smaller problem |
| **Swing** (`JFrame`, `JButton`, ...) | `MainFrame` | Java's built-in toolkit for windows |
| **ActionListener** `e -> ...` | `MainFrame` | "When this button is clicked, run this code" |
| **SwingWorker** | `MainFrame.runSort` | Runs slow work in the background so the window does not freeze |

Everything above is part of standard Java. **No extra libraries, no Maven, no JUnit.**

---

## Part 3 - Step-by-step guide for each member

### Everyone - first-time setup

1. Install **JDK 17 or newer** and check: `java -version` and `javac -version`
2. Clone the repo and switch to **your** branch (see `docs/GIT_WORKFLOW.md`)
3. Use any editor (IntelliJ IDEA Community, Eclipse, NetBeans or VS Code). Mark the `src/main/java`
   folder as the source folder. IntelliJ: *File > New > Project from Existing Sources*.
4. Make sure the project compiles before you change anything (commands in Part 4).

### Member 1 - File upload and dataset handling

**Your files:** `data/CsvLoader.java`, `data/ColumnValidator.java` (`data/Dataset.java` is shared)

**Step by step**

1. Read `Dataset.java` - it is just two lists: headers and rows (each row is a `String[]`).
2. Read `CsvLoader.load()` top to bottom. It has three phases: *check the file*, *read line by line*,
   *check we got data*.
3. Understand `parseLine()` - it walks through the line character by character. A comma inside
   double quotes does not split the cell. (`"Perera, Kamal"` stays one cell.)
4. Read `ColumnValidator`. Rules: blank cells are ignored; any non-number makes the column invalid;
   an all-blank column is invalid.
5. Test with the console program (no GUI needed):
   ```
   java -cp out lk.ac.uoc.sortapp.ConsoleCheck sample-data/sample_small.csv
   java -cp out lk.ac.uoc.sortapp.ConsoleCheck sample-data/bad_empty.csv
   java -cp out lk.ac.uoc.sortapp.ConsoleCheck sample-data/bad_header_only.csv
   ```
6. Download 2-3 real CSVs from Kaggle and test them too.
7. Commit in small steps and open a pull request into `develop`.

**Be ready to explain:** why we use `try (...)` (auto-close the file), what the BOM is, how quoted
cells work, why blank cells are skipped, which exceptions you throw and why.

**Edge cases you cover:** no file, missing file, wrong extension, empty file, header-only file,
short/long rows, blank lines, text in a numeric column, all-blank column.

### Member 2 - Sorting algorithms (group leader)

**Your files:** everything in `sorting/` and `src/test/.../SortingCheck.java`

**Step by step** - do them in this order, one commit each:

1. Read `SortingAlgorithm.java`. Every algorithm sorts the array **in place**, ascending.
2. **Insertion Sort** - easiest. Draw `[5, 2, 4, 1]` on paper and follow the code.
3. **Shell Sort** - same as insertion sort but with gaps (1, 4, 13, 40...).
4. **Merge Sort** - split, sort halves, merge. Notice the helper array is created once.
5. **Quick Sort** - pivot + partition. Read the three safety notes at the top of the file.
6. **Heap Sort** - build a max heap, then move the biggest value to the end repeatedly.
7. Run the test program after each one (it checks all five against `Arrays.sort`):
   ```
   java -cp out lk.ac.uoc.sortapp.sorting.SortingCheck
   ```
   Look for `ALL TESTS PASSED`.
8. Open a pull request into `develop` as early as possible - Member 3 needs your classes.

**Algorithm cheat sheet**

| Algorithm | Idea | Average time | Worst time | Extra memory |
|---|---|---|---|---|
| Insertion | insert each item into the sorted part | O(n^2) | O(n^2) | O(1) |
| Shell | insertion sort with shrinking gaps | ~O(n^1.5) | ~O(n^1.5) | O(1) |
| Merge | split, sort, merge | O(n log n) | O(n log n) | O(n) |
| Quick | pivot + partition | O(n log n) | O(n^2) (avoided here) | O(log n) |
| Heap | max heap, swap max to end | O(n log n) | O(n log n) | O(1) |

**Be ready to explain:** each algorithm in your own words on a small example, why Insertion Sort is
slow on big data, why Quick Sort uses median-of-three, why we never use `Arrays.sort` inside the
algorithms, and your Shell Sort gap sequence.

### Member 3 - Performance evaluation and GUI

**Your files:** `benchmark/BenchmarkRunner.java`, `benchmark/BenchmarkResult.java`, `ui/MainFrame.java`, `Main.java`

**Step by step**

1. Read `BenchmarkRunner.run()`. For each algorithm: warm up, clone the data, record
   `System.nanoTime()` before and after `sort()`, store a `BenchmarkResult`.
2. Read `findBest()` - a simple loop that keeps the smallest `getNanos()`.
3. Test the benchmark without the GUI first:
   ```
   java -cp out lk.ac.uoc.sortapp.ConsoleCheck sample-data/sample_large.csv
   ```
4. Read `MainFrame` in this order: fields (the components) -> constructor -> `buildTopPanel`,
   `buildCenterPanel`, `buildBottomPanel` (layout) -> `chooseFile` -> `runSort` -> `showResults`.
5. Run the application and try every button (Part 4).
6. While Members 1 and 2 are still working, you can build against the stubs/sample data. Pull
   `develop` often so you get their finished code.

**Be ready to explain:** why each algorithm gets a copy (`clone()`), why only the sort call is timed,
what warm-up is, what `nanoTime()` returns, why `SwingWorker` is used, how a button click reaches your code.

**Edge cases you cover:** no file loaded, no column chosen, non-numeric column, empty column,
sorting error, window stays responsive while sorting.

---

## Part 4 - Compile and run (no Maven needed)

From the project root folder.

**Windows (Command Prompt):**
```
mkdir out
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
java -cp out lk.ac.uoc.sortapp.Main
```

**Mac / Linux:**
```
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out lk.ac.uoc.sortapp.Main
```

Test programs (same `out` folder):
```
java -cp out lk.ac.uoc.sortapp.sorting.SortingCheck
java -cp out lk.ac.uoc.sortapp.ConsoleCheck sample-data/sample_small.csv
```

In an IDE you can simply right-click `Main.java` and choose Run. The `out/` folder and `sources.txt`
should not be committed - add them to `.gitignore` (`out/` and `sources.txt`).

---

## Part 5 - Integration and demo checklist

When all three branches are merged into `develop`:

1. Compile from a **fresh clone** and run `Main`.
2. Load `sample_small.csv` -> columns appear, numeric ones are marked.
3. Sort the `Age` column -> five times, one `BEST`, preview shows smallest/largest values.
4. Select `Name` (text) -> a warning appears, nothing crashes.
5. Load `bad_empty.csv`, `bad_header_only.csv` and a `.txt` file -> friendly error each time.
6. Load `sample_large.csv` and sort `salary` -> window stays responsive; Insertion Sort is clearly slowest.
7. Test at least one real Kaggle file.

## Part 6 - Presentation reminders

- Each member says: *"I'm <name>, my student number is <number>."* before explaining their part.
- Explain **your own** code line by line on screen, then show it working.
- Demo order: upload file -> columns shown -> select column -> sort -> times and best algorithm -> one error case.
- Rehearse once before recording.

## Design decision to mention

The application sorts the **values of the selected column** (an array of numbers) and compares the
five algorithms on that array. It shows the smallest and largest values after sorting so correctness
is visible. Say this clearly in the presentation.
