package practical;

import java.util.*;

public class EdmondsKarpBipartiteMatching {

    static int vertices;

    static boolean bfs(int[][] graph, int source, int sink, int[] parent) {
        boolean[] visited = new boolean[vertices];
        Queue<Integer> queue = new LinkedList<>();

        queue.add(source);
        visited[source] = true;
        parent[source] = -1;

        while (!queue.isEmpty()) {
            int u = queue.poll();

            for (int v = 0; v < vertices; v++) {
                if (!visited[v] && graph[u][v] > 0) {
                    queue.add(v);
                    parent[v] = u;
                    visited[v] = true;

                    if (v == sink) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static int edmondsKarp(int[][] graph, int source, int sink) {
        int[][] residualGraph = new int[vertices][vertices];

        for (int i = 0; i < vertices; i++) {
            for (int j = 0; j < vertices; j++) {
                residualGraph[i][j] = graph[i][j];
            }
        }

        int[] parent = new int[vertices];
        int maxFlow = 0;

        while (bfs(residualGraph, source, sink, parent)) {
            int pathFlow = Integer.MAX_VALUE;

            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                pathFlow = Math.min(pathFlow, residualGraph[u][v]);
            }

            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                residualGraph[u][v] -= pathFlow;
                residualGraph[v][u] += pathFlow;
            }

            maxFlow += pathFlow;
        }

        return maxFlow;
    }

    public static void bipartiteMatching() {
        String[] students = {
            "Student 1",
            "Student 2",
            "Student 3"
        };

        String[] projects = {
            "Project 1",
            "Project 2",
            "Project 3"
        };

        int studentCount = students.length;
        int projectCount = projects.length;

        int source = 0;
        int studentStart = 1;
        int projectStart = studentStart + studentCount;
        int sink = projectStart + projectCount;
        int totalVertices = sink + 1;

        int[][] graph = new int[totalVertices][totalVertices];

        for (int i = 0; i < studentCount; i++) {
            graph[source][studentStart + i] = 1;
        }

        graph[studentStart][projectStart] = 1;
        graph[studentStart][projectStart + 1] = 1;

        graph[studentStart + 1][projectStart + 1] = 1;
        graph[studentStart + 1][projectStart + 2] = 1;

        graph[studentStart + 2][projectStart] = 1;

        for (int i = 0; i < projectCount; i++) {
            graph[projectStart + i][sink] = 1;
        }

        vertices = totalVertices;

        int maxMatching = edmondsKarp(graph, source, sink);

        System.out.println("\n======================================");
        System.out.println("      BIPARTITE MATCHING");
        System.out.println("      USING EDMONDS-KARP");
        System.out.println("======================================");

        System.out.println("\nStudents:");
        for (String student : students) {
            System.out.println(" - " + student);
        }

        System.out.println("\nProjects:");
        for (String project : projects) {
            System.out.println(" - " + project);
        }

        System.out.println("\nPossible Assignments:");
        System.out.println("Student 1 -> Project 1 / Project 2");
        System.out.println("Student 2 -> Project 2 / Project 3");
        System.out.println("Student 3 -> Project 1");

        System.out.println("\nMaximum Matching (Optimal Assignment) = " + maxMatching);
    }

    public static void runFlowDemo() {
        System.out.println("======================================");
        System.out.println("       EDMONDS-KARP MAX FLOW DEMO");
        System.out.println("======================================");

        int[][] graph = {
            // S   A   B   C   D   T
            {  0, 10,  5,  0,  0,  0 }, // S
            {  0,  0,  5, 10,  0,  0 }, // A
            {  0,  0,  0,  0, 10,  0 }, // B
            {  0,  0,  0,  0,  5, 10 }, // C
            {  0,  0,  0,  0,  0, 10 }, // D
            {  0,  0,  0,  0,  0,  0 }  // T
        };

        vertices = graph.length;
        int source = 0;
        int sink = 5;

        int maxFlow = edmondsKarp(graph, source, sink);
        System.out.println("\nSource Node: S (0), Sink Node: T (5)");
        System.out.println("Calculated Maximum Flow = " + maxFlow);
    }

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        run(s);
        s.close();
    }

    public static void run(Scanner s) {
        while (true) {
            System.out.println("\n==============================================");
            System.out.println("        EDMONDS-KARP & BIPARTITE MATCHING");
            System.out.println("==============================================");
            System.out.println("1. Run Flow Network Max-Flow Demo");
            System.out.println("2. Run Bipartite Matching (Student-Project Allocation)");
            System.out.println("3. Run Both Demos");
            System.out.println("4. Return to Main Menu");
            System.out.print("Enter choice: ");

            String input = s.nextLine().trim();
            if (input.equals("1")) {
                runFlowDemo();
            } else if (input.equals("2")) {
                bipartiteMatching();
            } else if (input.equals("3")) {
                runFlowDemo();
                bipartiteMatching();
            } else if (input.equals("4") || input.equalsIgnoreCase("exit")) {
                System.out.println("\nReturning to Main Menu...");
                break;
            } else {
                System.out.println("Invalid choice. Please enter 1, 2, 3, or 4.");
            }
        }
    }
}
