import java.util.*;

public class FinishedTerrainGrid {
    // Terrain costs
    final static int P=2, F=3, S=5, M=Integer.MAX_VALUE;

    static class Node {
        int x, y, g, h, f;
        Node parent;
        Node(int x, int y) { this.x = x; this.y = y; }
    }

    public static void main(String[] args) {
        char[][] grid = {
                {'S', 'p', 'p', 'f', 'G'},
                {'f', 'm', 'f', 'f', 'f'},
                {'p', 'p', 's', 'p', 'p'}
        };
        findPath(grid);
    }

    static void findPath(char[][] grid) {
        Node start = null, goal = null;
        // Find start (S) and goal (G)
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 'S') start = new Node(i, j);
                if (grid[i][j] == 'G') goal = new Node(i, j);
            }
        }

        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));
        Set<String> closed = new HashSet<>();
        start.g = 0;
        start.h = Math.abs(start.x-goal.x) + Math.abs(start.y-goal.y);
        start.f = start.g + start.h;
        open.add(start);

        int nodesExplored = 0;
        while (!open.isEmpty()) {
            Node current = open.poll();
            nodesExplored++;

            if (current.x == goal.x && current.y == goal.y) {
                printResult(current, nodesExplored);
                return;
            }

            closed.add(current.x+","+current.y);

            for (Node neighbor : getNeighbors(current, grid)) {
                if (closed.contains(neighbor.x+","+neighbor.y)) continue;

                int newG = current.g + getCost(grid[neighbor.x][neighbor.y]);
                neighbor.g = newG;
                neighbor.h = Math.abs(neighbor.x-goal.x) + Math.abs(neighbor.y-goal.y);
                neighbor.f = neighbor.g + neighbor.h;
                neighbor.parent = current;
                open.add(neighbor);
            }
        }
        System.out.println("No path found");
    }

    static List<Node> getNeighbors(Node n, char[][] grid) {
        List<Node> neighbors = new ArrayList<>();
        int[][] dirs = {{0,1},{1,0},{0,-1},{-1,0}};
        for (int[] d : dirs) {
            int x = n.x + d[0], y = n.y + d[1];
            if (x >= 0 && x < grid.length && y >= 0 && y < grid[0].length &&
                    Character.toLowerCase(grid[x][y]) != 'm') {
                neighbors.add(new Node(x, y));
            }
        }
        return neighbors;
    }

    static int getCost(char c) {
        c = Character.toLowerCase(c);
        if (c == 's') return S;
        if (c == 'f') return F;
        if (c == 'p') return P;
        return P; // Treat start/goal as plains
    }

    static void printResult(Node goal, int nodesExplored) {
        List<Node> path = new ArrayList<>();
        int cost = goal.g;
        for (Node n = goal; n != null; n = n.parent) path.add(n);
        Collections.reverse(path);

        System.out.println("Nodes explored: " + nodesExplored);
        System.out.println("Path length: " + (path.size()-1));
        System.out.print("Path: ");
        for (Node n : path) System.out.print("("+n.x+","+n.y+") ");
        System.out.println("\nTotal cost: " + cost);
    }
}