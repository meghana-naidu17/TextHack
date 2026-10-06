# TextHack: Advanced Text Analytics, Graph Algorithms & Optimization Suite

An integrated, high-performance Data Structures and Algorithms (DSA) project for text processing, exact and fuzzy string matching, dynamic programming sequence alignment, corpus indexing, graph optimization, probabilistic primality testing, universal hashing, and parallel prefix computation.

---

## Table of Contents
- [Overview](#overview)
- [Architecture & Algorithms](#architecture--algorithms)
- [Algorithm Complexity Reference](#algorithm-complexity-reference)
- [Dataset Details](#dataset-details)
- [Project Structure](#project-structure)
- [Quick Start: Build & Run](#quick-start-build--run)
- [Interactive Menu Options](#interactive-menu-options)
- [Bug Fixes & Refactoring Log](#bug-fixes--refactoring-log)

---

## Overview

**TextHack** brings together essential computer science, graph theory, and NLP algorithms in a unified, production-ready Java application. It provides both standalone execution for individual algorithm classes and a central, menu-driven CLI (`Main.java`) with zero compilation errors, smooth Scanner lifecycle management, and automatic dataset discovery across directories.

---

## Architecture & Algorithms

### 1. Knuth-Morris-Pratt (KMP) Pattern Matching (`practical.KMPapplied`)
- **Purpose**: Fast substring pattern search in large texts without backtracking.
- **Mechanism**: Builds a Longest Prefix Suffix (LPS) π-array in $O(m)$ time and searches the body text in $O(n)$ time.
- **Corpus Analytics**: Computes character counts, word counts, unique vocabulary, frequency maps, and most/least frequent terms.

### 2. Rabin-Karp Search & Jaccard Document Similarity (`practical.RabinKarpSearchDoc`)
- **Purpose**: Rolling-hash substring matching and set-theoretic document similarity.
- **Mechanism**:
  - Hash computation: Base-256 rolling polynomial hash modulo prime 101.
  - Document Similarity: Jaccard coefficient $J(A, B) = \frac{|A \cap B|}{|A \cup B|}$ on tokenized word sets.

### 3. Fuzzy Search & Spell Suggestion (`practical.FuzzySearch`)
- **Purpose**: Approximate string matching and "did you mean" query suggestions.
- **Mechanism**: Dynamic programming Levenshtein edit distance with adaptive distance thresholds based on query length. Indexes over 16,000 unique entity names across movies, anime, characters, and Pokemon.

### 4. Needleman-Wunsch Sequence Alignment (`practical.NeedlemanWunch`)
- **Purpose**: Global sequence alignment between strings or DNA sequences.
- **Mechanism**: DP matrix scoring ($+1$ match, $-1$ mismatch, $-2$ gap penalty) with optimal traceback. Displays the full dynamic programming scoring matrix.

### 5. Article Storage & Query Engine (`practical.ArticleStorageQuer`)
- **Purpose**: Document repository indexing, metadata extraction, title lookup, and keyword query searching across the text corpus.

### 6. Corpus Loading & Vocabulary Statistics (`practical.CorpusLoadingArticel`)
- **Purpose**: In-depth lexical statistics, word distributions, and token frequencies for loaded text corpora.

### 7. Citation Flow Analysis via Ford-Fulkerson (`practical.FordFulkersonCitationFlow`)
- **Purpose**: Modeling scholarly influence, citation authority propagation, and bottleneck capacity in academic citation networks using the Ford-Fulkerson Max-Flow algorithm.

### 8. Edmonds-Karp Bipartite Matching (`practical.EdmondsKarpBipartiteMatching`)
- **Purpose**: Optimal resource allocation (e.g., student-to-project allocation or reviewer-to-paper matching) modeled as maximum network flow using BFS-based augmenting paths.

### 9. Minimum Document Set Cover (`practical.ExactSetCover`)
- **Purpose**: Finding the minimal subset of documents required to cover all target search terms using a recursive backtracking optimization with branch pruning.

### 10. Vertex Cover 2-Approximation & Scheduling Demonstration (`practical.VertexCoverApproximation`) [CO5]
- **Purpose**: Approximation algorithm for the NP-complete Minimum Vertex Cover problem and application to conflict scheduling.
- **Mechanism**:
  - Repeatedly picks an uncovered edge $(u, v)$, adds *both* endpoints to cover $C$, and discards incident edges.
  - Provable theoretical guarantee: $|C| \le 2 \times |C^*|$.
  - Evaluates standard graphs (Cycle C5, Star S6, Petersen Graph) comparing 2-approximation with exact optimal covers.
  - **Real-World Scheduling**: Resolves examination/workshop scheduling conflicts by determining the minimal proctor stations to monitor all conflicting course pairs.

### 11. Miller-Rabin Primality Testing & Randomized Hashing (`practical.MillerRabinAndRandomizedHashing`) [CO6]
- **Purpose**: Fast probabilistic primality testing and collision-resistant randomized universal hashing.
- **Mechanism**:
  - **Miller-Rabin Test**: Decomposes $n - 1 = 2^s \cdot d$ and tests random bases $a \in [2, n-2]$ with modular exponentiation. Error probability $\le (1/4)^k$ after $k$ rounds. Benchmarked against trial division (achieving up to 1300x speedup on 64-bit primes) and validates 256-bit cryptographic numbers.
  - **2-Universal Hashing**: Carter-Wegman universal hash family $h_{a,b}(x) = ((a \cdot x + b) \bmod p) \bmod m$. Dynamically hashes thousands of dataset tokens to verify uniform bucket distribution and defend against hash-flooding DoS attacks.

### 12. Parallel Prefix-Sum & Multi-Core Benchmarking (`practical.ParallelPrefixSumBenchmark`) [CO6]
- **Purpose**: Work-efficient parallel prefix computation (scan) and high-throughput multi-core performance benchmarking.
- **Mechanism**:
  - **Algorithm**: Multi-threaded block scan with Phase 1 (parallel local scan & block reduction), Phase 2 (sequential block offsets scan), and Phase 3 (parallel offset distribution).
  - **Benchmarking Suite**: Evaluates arrays up to 10,000,000 elements comparing Sequential $O(n)$, Custom Multi-Threaded ForkJoin, and Java's built-in `Arrays.parallelPrefix`, reporting execution time (ms), throughput (MOps/sec), and speedup factor.

---

## Algorithm Complexity Reference

| Algorithm / Feature | Primary Class | Course Outcome | Time Complexity | Space Complexity |
|---|---|---|---|---|
| **KMP Pattern Matching** | `KMPapplied` | - | $O(n + m)$ | $O(m)$ |
| **Rabin-Karp Substring Search** | `RabinKarpSearchDoc` | - | Average: $O(n + m)$, Worst: $O(n \times m)$ | $O(1)$ auxiliary |
| **Jaccard Similarity** | `RabinKarpSearchDoc` | - | $O(V_1 + V_2)$ | $O(V_1 + V_2)$ |
| **Levenshtein Distance (Fuzzy)** | `FuzzySearch` | - | $O(m \times n)$ per candidate | $O(m \times n)$ |
| **Needleman-Wunsch Alignment** | `NeedlemanWunch` | - | $O(m \times n)$ | $O(m \times n)$ |
| **Ford-Fulkerson Max Flow** | `FordFulkersonCitationFlow` | - | $O(E \times \text{max\_flow})$ | $O(V^2)$ residual graph |
| **Edmonds-Karp Max Flow** | `EdmondsKarpBipartiteMatching` | - | $O(V \times E^2)$ | $O(V^2)$ residual graph |
| **Exact Set Cover** | `ExactSetCover` | - | $O(2^D \times T)$ (Pruned) | $O(D + T)$ |
| **Vertex Cover 2-Approx** | `VertexCoverApproximation` | **CO5** | $O(V + E)$ | $O(V + E)$ |
| **Miller-Rabin Primality** | `MillerRabinAndRandomizedHashing` | **CO6** | $O(k \cdot \log^3 n)$ | $O(\log n)$ |
| **Universal Randomized Hash** | `MillerRabinAndRandomizedHashing` | **CO6** | $O(\text{len})$ evaluation | $O(1)$ auxiliary |
| **Parallel Prefix-Sum** | `ParallelPrefixSumBenchmark` | **CO6** | Work: $O(n)$, Span: $O(\log n)$ | $O(n)$ output |

*(Where $n$ = text/array length, $m$ = pattern length, $V$ = vertices, $E$ = edges, $D$ = documents, $T$ = terms, $k$ = witness rounds)*

---

## Dataset Details

Located in the `dataset/` directory:
- `animetxt.txt`: 12,290+ anime titles, ratings, genres, and metadata.
- `characterstxt.txt`: Demon Slayer characters, abilities, and combat styles.
- `human.txt`: Real-world human genomic DNA sequence fragments.
- `moviedatatxt.txt`: 3,400+ Indian cinema box office records and release details.
- `pokemontxt.txt`: 800+ Pokemon species, elemental types, and evolution chains.

---

## Project Structure

```
TextHack/
├── dataset/                         # Raw text corpus files
│   ├── animetxt.txt
│   ├── characterstxt.txt
│   ├── human.txt
│   ├── moviedatatxt.txt
│   └── pokemontxt.txt
├── src/
│   ├── Main.java                    # Root launcher delegating to practical.Main
│   └── practical/
│       ├── ArticleStorageQuer.java
│       ├── CorpusLoadingArticel.java
│       ├── DatasetHelper.java       # Automatic dataset directory locator
│       ├── EdmondsKarpBipartiteMatching.java
│       ├── ExactSetCover.java
│       ├── FordFulkersonCitationFlow.java
│       ├── FuzzySearch.java
│       ├── KMPapplied.java
│       ├── Main.java                # Central interactive switch-case menu (1-13)
│       ├── MillerRabinAndRandomizedHashing.java    # CO6 Primality & Universal Hashing
│       ├── NeedlemanWunch.java
│       ├── ParallelPrefixSumBenchmark.java         # CO6 Parallel Prefix-Sum Scan
│       ├── RabinKarpSearchDoc.java
│       └── VertexCoverApproximation.java           # CO5 2-Approx & Scheduling
├── bin/                             # Compiled Java class binaries
├── build.bat                        # Windows 1-click build script
├── run.bat                          # Windows 1-click run script
└── README.md                        # Project documentation
```

---

## Quick Start: Build & Run

### Option 1: Using Batch Scripts (Windows)
Double-click `build.bat` to compile, then `run.bat` to start the application.

Or in Command Prompt:
```cmd
build.bat
run.bat
```

### Option 2: Using Standard Terminal Commands
```powershell
# Compile all files
javac -d bin -sourcepath src src/practical/*.java src/Main.java

# Run the central interactive switch-case menu
java -cp bin practical.Main
```

### Option 3: Running Individual Algorithms Standalone
Each class retains its own `public static void main(String[] args)`:
```powershell
java -cp bin practical.VertexCoverApproximation
java -cp bin practical.MillerRabinAndRandomizedHashing
java -cp bin practical.ParallelPrefixSumBenchmark
java -cp bin practical.KMPapplied
java -cp bin practical.FuzzySearch
java -cp bin practical.NeedlemanWunch
java -cp bin practical.RabinKarpSearchDoc
java -cp bin practical.EdmondsKarpBipartiteMatching
```

---

## Interactive Menu Options

When running `practical.Main`, the application presents a central switch-case dashboard:

```
+==================================================================+
|                 TEXTHACK - ALGORITHMS & NLP SUITE                |
|       Advanced Text Processing, Matching & Graph Analytics       |
+==================================================================+

                     --- MAIN MENU ---
 [1]  KMP Pattern Matching & Corpus Frequency Analysis
 [2]  Rabin-Karp Substring Search & Jaccard Document Similarity
 [3]  Fuzzy Search & Spell Suggestion (Levenshtein Distance)
 [4]  Needleman-Wunsch Sequence Alignment (Dynamic Programming)
 [5]  Article Storage, Indexing & Query Engine
 [6]  Corpus Loading & Vocabulary Statistics
 [7]  Citation Flow Analysis (Ford-Fulkerson Max Flow)
 [8]  Bipartite Matching & Resource Allocation (Edmonds-Karp)
 [9]  Exact Set Cover Document Optimization (Backtracking)
 [10] Vertex Cover 2-Approximation & Scheduling Demo (CO5)
 [11] Miller-Rabin Primality Testing & Randomized Hashing (CO6)
 [12] Parallel Prefix-Sum & Performance Benchmarking (CO6)
 [13] System Diagnostics & Dataset Verification
 [0]  Exit TextHack
```

---

## Bug Fixes & Refactoring Log

1. **Compilation Failure (`RabinKarpSearchDoc.java`)**:
   - Fixed public class declaration mismatch (`public class RabinkarpSearchDoc` changed to `public class RabinKarpSearchDoc`).
2. **Dataset Path Discrepancy**:
   - Built [`DatasetHelper.java`](src/practical/DatasetHelper.java) to resolve `dataset/` dynamically from root, `src/`, or parent directories without hardcoded paths.
3. **Missing File Reference in FuzzySearch**:
   - Removed non-existent `"AllCombined.txt"` and dynamically loaded existing dataset files.
4. **Scanner Stream Closure Crashes**:
   - Refactored all submodules to accept a shared `Scanner` via `run(Scanner)` without closing `System.in`, preventing `NoSuchElementException` crashes when returning to the main menu.
5. **Windows-1252 Terminal Encoding**:
   - Sanitized UI banners and prompts to standard ASCII to guarantee clean rendering on all Windows command prompts and PowerShell environments.
6. **Curriculum Modules 10, 11, and 12 Added**:
   - Implemented Vertex Cover 2-Approximation with real-world Exam Conflict Scheduling (CO5).
   - Implemented Miller-Rabin Primality Testing with step-by-step trace and Carter-Wegman Universal Randomized Hashing (CO6).
   - Implemented Work-Efficient Parallel Prefix-Sum (Scan) with multi-core benchmarking suite (CO6).
