import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class GameTerrainGrid {
    public static void main(String[] args){
        findShortestPath(new char[][]{
                {'S', 'p', 'p', 'f', 'G'},
                {'f', 'm', 'f', 'f', 'f'},
                {'p', 'p', 's', 'p', 'p'}
        });
    }

    //f: g + h
    //g: cost from start
    //h: heuristic cost to goal
    public static class Node{
        int x, y, f, g, h;
        Node parent;
        char letter;
        public Node(int x, int y){
            this.x = x;
            this.y = y;
        }

        public Node(int[] coords){
            this.x = coords[0];
            this.y = coords[1];
        }

        public List<Integer> getCoordsList(){
            return List.of(x,y);
        }

        public int[] getCoords(){
            return new int[]{x,y};
        }

        public void setValues(int g, int h, char letter, Node parent){
            this.parent = parent;
            if(parent.g == Integer.MAX_VALUE || g == Integer.MAX_VALUE) {
                setValues(Integer.MAX_VALUE, h, letter);
                return;
            };
            setValues(g + parent.g, h, letter);
        }

        public void setValues(int g, int h, char letter){
            this.letter = letter;
            this.g = g;
            this.h = h;
            if(g == Integer.MAX_VALUE || g < 0){
                f = Integer.MAX_VALUE;
                return;
            }
            f = g + h;
        }
    }

    public static void findShortestPath(char[][] grid) {
        long startTime = System.nanoTime();

        Node start = new Node(findStart(grid));
        Node end = new Node(findEnd(grid));
        start.setValues(getTileCost(grid[start.x][start.y]), h(start, end), grid[start.x][start.y]);

        Node current = start;
        HashSet<List<Integer>> visited = new HashSet<>();

        ArrayList<Node> path = new ArrayList<>();
        path.add(start);
        while(!(current.x == end.x && current.y == end.y)){

            ArrayList<Node> neighbors = getNeighbors(current, grid);
            for(Node neighbor : neighbors){
                if(visited.contains(neighbor.getCoordsList())) {
                    neighbors.remove(neighbor);
                    continue;
                }
                neighbor.setValues(getTileCost(grid[neighbor.x][neighbor.y]), h(neighbor, end), grid[neighbor.x][neighbor.y], current);
                visited.add(neighbor.getCoordsList());
            }
            Node smallest = neighbors.getFirst();
            for(Node neighbor : neighbors){
                if (neighbor.f < smallest.f) smallest = neighbor;
            }
            current = smallest;
            path.add(current);
        }
        long endTime = System.nanoTime() - startTime;

        printResults(path, visited.size(), endTime);
    }

    public static void printResults(ArrayList<Node> path, int totalVisited, long endTime){
        StringBuilder pathString = new StringBuilder();
        for(Node node : path){
            pathString.append(String.valueOf(node.letter));
            pathString.append(",");
        }
        System.out.printf("\nNodes explored/vistied %s", totalVisited);
        System.out.printf("\nShortest Path Lenght %s", path.size());
        System.out.printf("\nShortest Path %s", pathString);
        System.out.printf("\nPath total cost %s", path.getLast().g);
        System.out.printf("\nTotal Time %s ms", endTime / 1000000.0);

    }

    public static ArrayList<Node> getNeighbors(Node current, char[][] grid) {
        ArrayList<Node> neighbors = new ArrayList<>();
        int[][] dirs = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};

        for (int[] dir : dirs) {
            int x = current.x + dir[0];
            int y = current.y + dir[1];

            if (x >= 0 && x < grid.length && y >= 0 && y < grid[0].length) {
                neighbors.add(new Node(x, y));
            }
        }
        return neighbors;
    }

    //heuristic
    public static int h(Node current, Node end){
        int h = 2 * Math.abs(current.x - end.x) + Math.abs(current.y - end.y);
        return h;
    }

    public static int getTileCost(char c){
        return switch (c) {
            case 'S', 'G', 'p' -> 2;
            case 's' -> 5;
            case 'f' -> 3;
            default -> Integer.MAX_VALUE;
        };
    }

    private static int[] findStart(char[][] grid){
        for(int i = 0; i < grid.length; i++){
            for(int j = 0; j < grid[i].length; j++){
                if(grid[i][j] == 'S') return new int[]{i,j};
            }
        }
        return new int[]{-1,-1};
    }

    private static int[] findEnd(char[][] grid){
        for(int i = 0; i < grid.length; i++){
            for(int j = 0; j < grid[i].length; j++){
                if(grid[i][j] == 'G') return new int[]{i,j};
            }
        }
        return new int[]{-1,-1};
    }
}
