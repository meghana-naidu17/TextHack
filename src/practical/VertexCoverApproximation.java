package practical;

import java.util.*;

/**
 * CO5: Implementation of Vertex Cover 2-Approximation and Scheduling Demonstration.
 *
 * Demonstrates:
 * 1. The greedy 2-approximation algorithm for Minimum Vertex Cover (NP-hard).
 *    Guaranteed |C| <= 2 * |C*| where C* is the optimal minimum vertex cover.
 * 2. Exact Minimum Vertex Cover solver for small graphs to verify the approximation factor.
 * 3. Real-world Scheduling Conflict Resolution:
 *    Courses/exams with shared classrooms/students represented as a conflict graph.
 *    The vertex cover determines the minimum conflict-monitoring stations required.
 * 4. Interactive custom graph creation and evaluation.
 */
public class VertexCoverApproximation {

    public static class Edge {
        public int u;
        public int v;

        public Edge(int u, int v) {
            this.u = Math.min(u, v);
            this.v = Math.max(u, v);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Edge)) return false;
            Edge edge = (Edge) o;
            return u == edge.u && v == edge.v;
        }

        @Override
        public int hashCode() {
            return Objects.hash(u, v);
        }

        @Override
        public String toString() {
            return "(" + u + " - " + v + ")";
        }
    }

    public static class Graph {
        public int vertices;
        public List<String> vertexLabels;
        public List<Edge> edges;

        public Graph(int vertices) {
            this.vertices = vertices;
            this.vertexLabels = new ArrayList<>();
            for (int i = 0; i < vertices; i++) {
                vertexLabels.add("V" + i);
            }
            this.edges = new ArrayList<>();
        }

        public Graph(List<String> labels) {
            this.vertices = labels.size();
            this.vertexLabels = new ArrayList<>(labels);
            this.edges = new ArrayList<>();
        }

        public void addEdge(int u, int v) {
            if (u >= 0 && u < vertices && v >= 0 && v < vertices && u != v) {
                Edge e = new Edge(u, v);
                if (!edges.contains(e)) {
                    edges.add(e);
                }
            }
        }

        public String getLabel(int u) {
            if (u >= 0 && u < vertexLabels.size()) {
                return vertexLabels.get(u);
            }
            return "V" + u;
        }
    }

    /**
     * Greedy 2-Approximation for Vertex Cover.
     * Selects an arbitrary uncovered edge (u, v), adds BOTH u and v to cover C,
     * and discards all edges incident to u or v.
     */
    public static Set<Integer> approximateVertexCover(Graph graph, boolean printSteps) {
        Set<Integer> cover = new LinkedHashSet<>();
        Set<Edge> uncoveredEdges = new HashSet<>(graph.edges);

        if (printSteps) {
            System.out.println("\n--- Step-by-Step 2-Approximation Execution ---");
        }

        int step = 1;
        while (!uncoveredEdges.isEmpty()) {
            // Pick an arbitrary uncovered edge
            Edge chosenEdge = uncoveredEdges.iterator().next();

            if (printSteps) {
                System.out.printf("Step %d: Selected edge %s [%s - %s]%n",
                        step++, chosenEdge, graph.getLabel(chosenEdge.u), graph.getLabel(chosenEdge.v));
            }

            // Add BOTH endpoints to the vertex cover
            cover.add(chosenEdge.u);
            cover.add(chosenEdge.v);

            // Remove all edges incident to chosenEdge.u or chosenEdge.v
            int u = chosenEdge.u;
            int v = chosenEdge.v;
            uncoveredEdges.removeIf(e -> e.u == u || e.v == u || e.u == v || e.v == v);

            if (printSteps) {
                System.out.printf("   -> Added vertices to cover: {%s, %s}%n",
                        graph.getLabel(u), graph.getLabel(v));
                System.out.printf("   -> Remaining uncovered edges: %d%n", uncoveredEdges.size());
            }
        }

        return cover;
    }

    /**
     * Exact Minimum Vertex Cover using bitmask subset enumeration (for small graphs <= 20 vertices).
     */
    public static Set<Integer> exactMinimumVertexCover(Graph graph) {
        int n = graph.vertices;
        if (n > 20) {
            System.out.println("(Graph too large for exact brute-force search; skipping exact solver)");
            return Collections.emptySet();
        }

        Set<Integer> optimalCover = null;
        int minSize = Integer.MAX_VALUE;

        int totalSubsets = 1 << n;
        for (int mask = 0; mask < totalSubsets; mask++) {
            int subsetSize = Integer.bitCount(mask);
            if (subsetSize >= minSize) continue;

            boolean isValidCover = true;
            for (Edge e : graph.edges) {
                boolean uIn = ((mask >> e.u) & 1) == 1;
                boolean vIn = ((mask >> e.v) & 1) == 1;
                if (!uIn && !vIn) {
                    isValidCover = false;
                    break;
                }
            }

            if (isValidCover) {
                minSize = subsetSize;
                optimalCover = new TreeSet<>();
                for (int i = 0; i < n; i++) {
                    if (((mask >> i) & 1) == 1) {
                        optimalCover.add(i);
                    }
                }
            }
        }

        return optimalCover != null ? optimalCover : Collections.emptySet();
    }

    /**
     * Demo 1: Benchmark standard graphs (Petersen Graph, Star Graph, Cycle Graph).
     */
    public static void runBenchmarkDemo() {
        System.out.println("==================================================================");
        System.out.println("     BENCHMARK: 2-APPROXIMATION VS OPTIMAL ON STANDARD GRAPHS     ");
        System.out.println("==================================================================");

        // 1. Cycle Graph C5
        Graph c5 = new Graph(5);
        c5.addEdge(0, 1);
        c5.addEdge(1, 2);
        c5.addEdge(2, 3);
        c5.addEdge(3, 4);
        c5.addEdge(4, 0);

        evaluateGraph("Cycle Graph C5 (Odd Cycle)", c5);

        // 2. Star Graph S6 (Center = 0, Spoke = 1,2,3,4,5)
        Graph star = new Graph(6);
        for (int i = 1; i <= 5; i++) {
            star.addEdge(0, i);
        }
        evaluateGraph("Star Graph S6 (Extreme Case: Center node covers all)", star);

        // 3. Petersen Graph (10 vertices, 15 edges)
        Graph petersen = new Graph(10);
        // Outer 5-cycle
        for (int i = 0; i < 5; i++) {
            petersen.addEdge(i, (i + 1) % 5);
        }
        // Inner 5-star
        for (int i = 0; i < 5; i++) {
            petersen.addEdge(5 + i, 5 + ((i + 2) % 5));
        }
        // Spokes connecting outer to inner
        for (int i = 0; i < 5; i++) {
            petersen.addEdge(i, 5 + i);
        }
        evaluateGraph("Petersen Graph (10 vertices, 15 edges)", petersen);
    }

    private static void evaluateGraph(String name, Graph graph) {
        System.out.println("\n--------------------------------------------------");
        System.out.println("Evaluating: " + name);
        System.out.printf("Vertices: %d, Edges: %d%n", graph.vertices, graph.edges.size());
        System.out.print("Edges: ");
        for (Edge e : graph.edges) {
            System.out.print(e + " ");
        }
        System.out.println();

        Set<Integer> approx = approximateVertexCover(graph, false);
        Set<Integer> exact = exactMinimumVertexCover(graph);

        System.out.printf("2-Approximation Cover Size : %d vertices %s%n", approx.size(), formatCover(graph, approx));
        System.out.printf("Exact Minimum Cover Size   : %d vertices %s%n", exact.size(), formatCover(graph, exact));

        if (!exact.isEmpty()) {
            double ratio = (double) approx.size() / exact.size();
            System.out.printf("Approximation Ratio (|C| / |C*|) : %.2f (Guaranteed <= 2.00)%n", ratio);
        }
    }

    private static String formatCover(Graph graph, Set<Integer> cover) {
        StringBuilder sb = new StringBuilder("{");
        int count = 0;
        for (int v : cover) {
            if (count++ > 0) sb.append(", ");
            sb.append(graph.getLabel(v));
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Demo 2: Real-World Exam & Workshop Conflict Scheduling Demonstration.
     * Problem Statement:
     * In an academic institution, multiple examination sessions / workshops have scheduling conflicts
     * (e.g. shared student registrations, shared specialized laboratories, or common invigilators).
     * Two exams with a conflict cannot be unmonitored or held without a dedicated proctoring station.
     * Finding a Minimum Vertex Cover identifies the minimum set of monitored exam hubs
     * needed such that EVERY conflicting pair has at least one monitored exam hub.
     */
    public static void runSchedulingDemo() {
        System.out.println("==================================================================");
        System.out.println("        REAL-WORLD SCHEDULING CONFLICT RESOLUTION DEMO           ");
        System.out.println("==================================================================");

        List<String> exams = Arrays.asList(
            "CS301-Algorithms",       // 0
            "CS302-DatabaseSystems",  // 1
            "CS303-ComputerNetworks", // 2
            "EC301-DigitalSignal",    // 3
            "MA301-DiscreteMath",     // 4
            "AI301-MachineLearning",  // 5
            "CS304-OperatingSystems"  // 6
        );

        Graph conflictGraph = new Graph(exams);

        // Define scheduling conflicts (shared students or shared high-performance compute labs)
        conflictGraph.addEdge(0, 1); // CS301 <-> CS302 (Shared Core CS Students)
        conflictGraph.addEdge(0, 4); // CS301 <-> MA301 (Shared Math Prerequisite)
        conflictGraph.addEdge(0, 5); // CS301 <-> AI301 (Shared Lab A)
        conflictGraph.addEdge(1, 2); // CS302 <-> CS303 (Shared Core CS Students)
        conflictGraph.addEdge(2, 6); // CS303 <-> CS304 (Shared Networking Lab)
        conflictGraph.addEdge(3, 4); // EC301 <-> MA301 (Shared Math Faculty)
        conflictGraph.addEdge(5, 6); // AI301 <-> CS304 (Shared GPU Server Cluster)
        conflictGraph.addEdge(1, 6); // CS302 <-> CS304 (Shared Database Server Lab)

        System.out.println("Exam Sessions (Nodes):");
        for (int i = 0; i < exams.size(); i++) {
            System.out.printf("  [%d] %s%n", i, exams.get(i));
        }

        System.out.println("\nConflicting Pairs (Edges requiring mutual monitoring):");
        for (Edge e : conflictGraph.edges) {
            System.out.printf("  * %s <---> %s%n",
                    conflictGraph.getLabel(e.u), conflictGraph.getLabel(e.v));
        }

        System.out.println("\nRunning 2-Approximation Algorithm to assign conflict monitoring stations...");
        Set<Integer> approxCover = approximateVertexCover(conflictGraph, true);
        Set<Integer> exactCover = exactMinimumVertexCover(conflictGraph);

        System.out.println("\n====================== SCHEDULING REPORT ======================");
        System.out.printf("Total Exam Sessions           : %d%n", conflictGraph.vertices);
        System.out.printf("Total Conflicting Pairs       : %d%n", conflictGraph.edges.size());
        System.out.printf("Approximated Proctor Stations : %d stations needed%n", approxCover.size());
        for (int u : approxCover) {
            System.out.printf("   [+] Proctor Station assigned to: %s%n", conflictGraph.getLabel(u));
        }

        System.out.printf("\nOptimal Minimum Stations      : %d stations%n", exactCover.size());
        for (int u : exactCover) {
            System.out.printf("   [*] Optimal Station: %s%n", conflictGraph.getLabel(u));
        }

        System.out.println("\nConclusion:");
        System.out.println("Every conflicting exam pair has at least one exam node covered by a proctor station.");
        System.out.println("The 2-approximation produces an immediate, verified solution within factor 2 of optimal.");
        System.out.println("===============================================================");
    }

    public static void runCustomGraph(Scanner scanner) {
        System.out.println("\n--- Custom Graph Input ---");
        System.out.print("Enter number of vertices: ");
        int n;
        try {
            n = Integer.parseInt(scanner.nextLine().trim());
            if (n <= 0) {
                System.out.println("Vertices must be > 0.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input!");
            return;
        }

        Graph customGraph = new Graph(n);
        System.out.println("Enter edges as 'u v' (0-indexed, e.g. '0 1'). Type 'done' when finished:");

        while (true) {
            System.out.print("Edge: ");
            String line = scanner.nextLine().trim();
            if (line.equalsIgnoreCase("done")) break;

            String[] parts = line.split("\\s+");
            if (parts.length >= 2) {
                try {
                    int u = Integer.parseInt(parts[0]);
                    int v = Integer.parseInt(parts[1]);
                    if (u >= 0 && u < n && v >= 0 && v < n && u != v) {
                        customGraph.addEdge(u, v);
                        System.out.printf("Added edge (%d - %d)%n", u, v);
                    } else {
                        System.out.printf("Invalid vertices! Must be in range [0, %d] and u != v.%n", n - 1);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter valid integers.");
                }
            } else {
                System.out.println("Enter two space-separated integers, or 'done'.");
            }
        }

        evaluateGraph("Custom User Graph", customGraph);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        run(scanner);
        scanner.close();
    }

    public static void run(Scanner scanner) {
        while (true) {
            System.out.println("\n==================================================================");
            System.out.println("   CO5: VERTEX COVER 2-APPROXIMATION & SCHEDULING DEMONSTRATION   ");
            System.out.println("==================================================================");
            System.out.println(" [1] Run Benchmark Standard Graphs (Cycle, Star, Petersen)");
            System.out.println(" [2] Run Real-World Exam & Workshop Conflict Scheduling Demo");
            System.out.println(" [3] Enter and Evaluate Custom Graph");
            System.out.println(" [4] Return to Main Menu");
            System.out.print("Enter choice: ");

            String input = scanner.nextLine().trim();
            if (input.equals("1")) {
                runBenchmarkDemo();
            } else if (input.equals("2")) {
                runSchedulingDemo();
            } else if (input.equals("3")) {
                runCustomGraph(scanner);
            } else if (input.equals("4") || input.equalsIgnoreCase("exit")) {
                System.out.println("\nReturning to Main Menu...");
                break;
            } else {
                System.out.println("Invalid choice! Please select 1, 2, 3, or 4.");
            }
        }
    }
}
