# Sorting Algorithm Performance Evaluation

IC4302 – Programming III · Group Assignment · Department of ICT, University of Colombo

A Java (Swing) application that loads a `.csv` file, lets the user choose a numeric column,
sorts it with five algorithms (Insertion, Shell, Merge, Quick, Heap), measures the execution
time of each and shows which one is fastest.

Plain Java only - no external libraries, no build tools.

## Team and responsibilities

| Member | Responsibility | Package | Branch |
|---|---|---|---|
| Member 1 | File upload + dataset handling | `data` | `member1/csv-loader` |
| Member 2 (Leader) | The 5 sorting algorithms | `sorting` | `member2/sorting` |
| Member 3 | Performance evaluation + GUI | `benchmark`, `ui` | `member3/benchmark-ui` |

> Fill in names and student numbers here.

## Requirements

- JDK 17 or newer

## Build and run

Mac / Linux:
```bash
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out lk.ac.uoc.sortapp.Main
```

Windows (Command Prompt):
```
mkdir out
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
java -cp out lk.ac.uoc.sortapp.Main
```

Test programs:
```
java -cp out lk.ac.uoc.sortapp.sorting.SortingCheck
java -cp out lk.ac.uoc.sortapp.ConsoleCheck sample-data/sample_small.csv
```

## Project structure

```
src/main/java/lk/ac/uoc/sortapp/
├── Main.java
├── data/        Dataset, CsvLoader, ColumnValidator      (Member 1)
├── sorting/     SortingAlgorithm + 5 implementations     (Member 2)
├── benchmark/   BenchmarkRunner, BenchmarkResult         (Member 3)
└── ui/          MainFrame (Swing GUI)                    (Member 3)
src/test/java/       SortingCheck, ConsoleCheck (simple test programs)
sample-data/         sample and deliberately bad CSV files for testing
docs/                BEGINNER_GUIDE.md, GIT_WORKFLOW.md
```

## Shared contracts (do not change without telling the group)

- `SortingAlgorithm` – `String getName()` and `void sort(double[] data)` (in place, ascending)
- `Dataset` – headers + raw string rows
- `BenchmarkResult` – algorithm name, time in nanoseconds, sorted copy

## Documentation

- [docs/BEGINNER_GUIDE.md](docs/BEGINNER_GUIDE.md) – how the application works, step-by-step guide per member
- [docs/GIT_WORKFLOW.md](docs/GIT_WORKFLOW.md) – branches and pull requests
