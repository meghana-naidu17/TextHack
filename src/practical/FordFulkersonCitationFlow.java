package practical;

import java.util.*;

public class FordFulkersonCitationFlow {

    static int vertices;

    static boolean bfs(int[][] residualGraph, int source, int sink, int[] parent) {
        boolean[] visited = new boolean[vertices];
        Queue<Integer> queue = new LinkedList<>();

        queue.add(source);
        visited[source] = true;
        parent[source] = -1;

        while (!queue.isEmpty()) {
            int u = queue.poll();

            for (int v = 0; v < vertices; v++) {
                if (!visited[v] && residualGraph[u][v] > 0) {
                    queue.add(v);
                    parent[v] = u;
                    visited[v] = true;
                }
            }
        }
        return visited[sink];
    }

    public static int fordFulkerson(int[][] graph, int source, int sink) {
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

    public static void runDemo() {
        String[] articles = {
            "Article A",
            "Article B",
            "Article C",
            "Article D",
            "Article E",
            "Article F"
        };

        vertices = articles.length;

        int[][] citationGraph = {
            // A   B   C   D   E   F
            {  0, 10, 10,  0,  0,  0 }, // A
            {  0,  0,  2,  8,  0,  0 }, // B
            {  0,  0,  0,  0,  6,  4 }, // C
            {  0,  0,  0,  0,  0, 10 }, // D
            {  0,  0,  0,  0,  0, 10 }, // E
            {  0,  0,  0,  0,  0,  0 }  // F
        };

        int source = 0;
        int sink = 5;

        System.out.println("======================================");
        System.out.println("   CITATION FLOW ANALYSIS");
        System.out.println("   FORD-FULKERSON ALGORITHM");
        System.out.println("======================================");

        System.out.println("\nArticles / Academic Nodes:");
        for (int i = 0; i < articles.length; i++) {
            System.out.println("  [" + i + "] " + articles[i]);
        }

        System.out.println("\nCitation Network Edges & Capacities:");
        for (int i = 0; i < vertices; i++) {
            for (int j = 0; j < vertices; j++) {
                if (citationGraph[i][j] > 0) {
                    System.out.println("  " + articles[i] + " -> " + articles[j] +
                            "  (Influence Capacity = " + citationGraph[i][j] + ")");
                }
            }
        }

        int maxFlow = fordFulkerson(citationGraph, source, sink);

        System.out.println("\nSource Article      : " + articles[source]);
        System.out.println("Destination Article : " + articles[sink]);
        System.out.println("\nMaximum Citation Flow = " + maxFlow);
    }

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        run(s);
        s.close();
    }

    public static void run(Scanner s) {
        runDemo();
        System.out.println("\nPress Enter to return to Main Menu...");
        s.nextLine();
    }
}
