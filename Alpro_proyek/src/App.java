import java.util.Scanner;
public class App {
    static int[] dr = {-1, 1, 0, 0}; // up, down, left, right
    static int[] dc = {0, 0, -1, 1};
    static int tries = 0;
    static int minSteps = Integer.MAX_VALUE;
    static char[][] bestMap = null;
    static boolean found = false;
    public static void main(String[] args) throws Exception {
        Scanner scInt = new Scanner(System.in);
        System.out.print("Select map to use: ");
        int mapChoice = scInt.nextInt();
        String mapFile;
        switch (mapChoice) {
            case 1:
                mapFile = "src/Z_array1.txt";
                break;
            case 2:
                mapFile = "src/Z_array2.txt";
                break;
            default:
                System.out.println("Invalid map selection.");
                scInt.close();
                return;
        }
        scInt.close();

        char [][] map = FileReader2DArray.read2DCharMapFromFile(mapFile);
        int[] start = findChar(map, 'P');
        if (start == null) {
            System.out.println("Player not found!");
            return;
        }

        boolean[][] visited = new boolean[map.length][map[0].length];
        backtrack(map, visited, start[0], start[1], 0, false);

        if (minSteps < Integer.MAX_VALUE) {
            System.out.println("Path found:");
            FileReader2DArray.print2DCharMap(bestMap);
            System.out.println("Tries: " + tries);
            System.out.println("Steps: " + minSteps);
        } else {
            System.out.println("No path found.");
            System.out.println("Tries: " + tries);
        }
    }

    // Find the position of a character in the map
    static int[] findChar(char[][] map, char target) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == target) return new int[]{i, j};
            }
        }
        return null;
    }

    // Backtracking function to find path from (r, c) to 'E', must get 'K' first
    static void backtrack(char[][] map, boolean[][] visited, int r, int c, int steps, boolean hasKey) {
        tries++;
        if (map[r][c] == 'E' && hasKey) {
            if (steps < minSteps) {
                minSteps = steps;
                // Save the best map
                bestMap = new char[map.length][map[0].length];
                for (int i = 0; i < map.length; i++) {
                    bestMap[i] = map[i].clone();
                }
            }
            return;
        }
        visited[r][c] = true;
        boolean pickedKey = false;
        if (map[r][c] == 'K' && !hasKey) {
            hasKey = true;
            pickedKey = true;
            System.out.println("Key Taken");
        }
        for (int d = 0; d < 4; d++) {
            int nr = r + dr[d], nc = c + dc[d];
            if (nr >= 0 && nr < map.length && nc >= 0 && nc < map[0].length &&
                !visited[nr][nc] && !FileReader2DArray.isWall(map, nr, nc)) {
                // Don't allow entering 'E' before getting the key
                if (map[nr][nc] == 'E' && !hasKey) continue;
                char temp = map[nr][nc];
                if (temp != 'E' && temp != 'P' && temp != 'K') map[nr][nc] = '*'; // Mark path
                backtrack(map, visited, nr, nc, steps + 1, hasKey);
                if (temp != 'E' && temp != 'P' && temp != 'K') map[nr][nc] = ' '; // Unmark if not correct path
            }
        }
        if (pickedKey) hasKey = false; // Backtrack key pickup
        visited[r][c] = false;
    }
}
