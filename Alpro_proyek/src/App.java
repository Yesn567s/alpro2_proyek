import java.util.Scanner;
import java.util.HashSet;

public class App {
    static int[] dr = { -1, 1, 0, 0 }; // up, down, left, right
    static int[] dc = { 0, 0, -1, 1 };
    static int tries = 0;
    static int minSteps = Integer.MAX_VALUE;
    static char[][] bestMap = null;
    static boolean found = false;
    static MapVisualizer visualizer;

    public static void main(String[] args) throws Exception {
        Scanner scInt = new Scanner(System.in);
        System.out.print("Select map to use: ");
        int mapChoice = scInt.nextInt();
        String mapFile;
        int initialHealth = 0;
        switch (mapChoice) {
            case 1:
                mapFile = "Alpro_proyek/src/Z_array1.txt";
                break;
            case 2:
                mapFile = "Alpro_proyek/src/Z_array2.txt";
                break;
            case 3:
                initialHealth = 200;
                mapFile = "Alpro_proyek/src/Z_array3.txt";
                break;
            case 4:
                initialHealth = 200;
                mapFile = "Alpro_proyek/src/Z_array4.txt";
                break;
            case 5:
                initialHealth = 200;
                mapFile = "Alpro_proyek/src/Z_array5.txt";
                break;
            default:
                System.out.println("Invalid map selection.");
                scInt.close();
                return;
        }
        scInt.close();

        char[][] map = FileReader2DArray.read2DCharMapFromFile(mapFile);
        int[] start = findChar(map, 'P');
        // Check start position
        if (start == null) {
            System.out.println("Player not found!"); // if 'P' is not found
            return;
        }

        // Set up for GUI
        if (mapChoice>2) {
            visualizer = new MapVisualizer(map, initialHealth); // for map 3-5
            visualizer.setVisible(true);
        } else {
            visualizer = new MapVisualizer(map); // for map 1 and 2
            visualizer.setVisible(true);
        }
        
        // Backtracking
        if (mapChoice == 3) {
            boolean[][] visited = new boolean[map.length][map[0].length];
            backtrackWithHealth(map, visited, start[0], start[1], 0, false, initialHealth);
        } else if (mapChoice == 4) {
            hasSword = false;
            hasPickaxe = false;
            gold = 0;
            bestHealth = initialHealth;
            boolean[][][][][] visited = new boolean[map.length][map[0].length][2][2][201]; // [row][col][pickaxe][sword][gold]
            backtrackMap4(map, visited, start[0], start[1], 0, false, initialHealth, false, false, 0);
        } else if (mapChoice == 5){
            HashSet<String> visited = new HashSet<>();
            bestHealth = initialHealth;
            char[][] pathMap = copyMap(map); // Make a copy for path marking
            backtrackMap5(map, visited, start[0], start[1], 0, false, initialHealth, false, false, 0, pathMap);
        }else{
            boolean[][] visited = new boolean[map.length][map[0].length];
            backtrack(map, visited, start[0], start[1], 0, false);
        }

        if (minSteps < Integer.MAX_VALUE) {
            System.out.println("Path found:");
            FileReader2DArray.print2DCharMap(bestMap);
            System.out.println("Tries: " + tries);
            System.out.println("Steps: " + minSteps);
            if (mapChoice == 3 || mapChoice == 4 || mapChoice == 5) {
                visualizer.updateMap(bestMap, tries, bestHealth);
                System.out.println("Remaining Health: " + bestHealth);
            }
        } else {
            System.out.println("No path found.");
            System.out.println("Tries: " + tries);
        }
    }

    // Find the position of a character in the map
    static int[] findChar(char[][] map, char target) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == target)
                    return new int[] { i, j };
                if (map[i][j] == target)
                    return new int[] { i, j };
            }
        }
        return null;
    }

    // Backtracking function to find path from (r, c) to 'E', must get 'K' first
    static void backtrack(char[][] map, boolean[][] visited, int r, int c, int steps, boolean hasKey) {
        tries++;
        visualizer.updateMap(map, tries);
        if (map[r][c] == 'E' && hasKey) {
            if (steps < minSteps) {
                minSteps = steps;
                // Save the best map
                bestMap = new char[map.length][map[0].length];
                for (int i = 0; i < map.length; i++) {
                    bestMap[i] = map[i].clone();
                }
            }
            visualizer.updateMap(bestMap, tries);
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
                if (map[nr][nc] == 'E' && !hasKey)
                    continue;
                if (map[nr][nc] == 'E' && !hasKey)
                    continue;
                char temp = map[nr][nc];
                if (temp != 'E' && temp != 'P' && temp != 'K')
                    map[nr][nc] = '*'; // Mark path
                if (temp != 'E' && temp != 'P' && temp != 'K')
                    map[nr][nc] = '*'; // Mark path
                backtrack(map, visited, nr, nc, steps + 1, hasKey);
                if (temp != 'E' && temp != 'P' && temp != 'K')
                    map[nr][nc] = ' '; // Unmark if not correct path
                if (temp != 'E' && temp != 'P' && temp != 'K')
                    map[nr][nc] = ' '; // Unmark if not correct path
            }
        }
        if (pickedKey)
            hasKey = false; // Backtrack key pickup
        visited[r][c] = false;
    }

    static int bestHealth = -1;

    // Backtracking for map 3 with health and lava
    static void backtrackWithHealth(char[][] map, boolean[][] visited, int r, int c, int steps, boolean hasKey,
            int health) {
        tries++;
        visualizer.updateMap(map, tries, health);
        if (health <= 0) {
            return;
        }
            
        if (map[r][c] == 'E' && hasKey) {
            if (steps < minSteps || (steps == minSteps && health > bestHealth)) {
                minSteps = steps;
                bestHealth = health;
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
        if (map[r][c] == 'L') {
            health -= 50;
        }
        for (int d = 0; d < 4; d++) {
            int nr = r + dr[d], nc = c + dc[d];
            if (nr >= 0 && nr < map.length && nc >= 0 && nc < map[0].length &&
                    !visited[nr][nc] && !FileReader2DArray.isWall(map, nr, nc)) {
                if (map[nr][nc] == 'E' && !hasKey)
                    continue;
                char temp = map[nr][nc];
                int nextHealth = health;
                if (temp == 'L')
                    nextHealth -= 50; // Reduce health if stepping into lava
                if (nextHealth <= 0)
                    continue; // Skip if dead
                if (temp != 'E' && temp != 'P' && temp != 'K' && temp != 'L')
                    map[nr][nc] = '*';
                backtrackWithHealth(map, visited, nr, nc, steps + 1, hasKey, nextHealth);
                if (temp != 'E' && temp != 'P' && temp != 'K' && temp != 'L')
                    map[nr][nc] = ' ';
            }
        }
        if (pickedKey)
            hasKey = false;
        visited[r][c] = false;
    }

    static boolean hasSword = false;
    static boolean hasPickaxe = false;
    static int gold = 0;

    static void backtrackMap4(
    char[][] map,
    boolean[][][][][] visited, // [row][col][pickaxe][sword][gold]
    int r, int c, int steps, boolean hasKey,
    int health, boolean hasPickaxe, boolean hasSword, int gold
    ) {
        visualizer.updateMap(map, tries, health);
        tries++;
        if (health <= 0) return;
        if (map[r][c] == 'E' && hasKey) {
            if (steps < minSteps || (steps == minSteps && health > bestHealth)) {
                minSteps = steps;
                bestHealth = health;
                bestMap = new char[map.length][map[0].length];
                for (int i = 0; i < map.length; i++)
                    bestMap[i] = map[i].clone();
            }
            return;
        }
        
        int pickaxeIdx = hasPickaxe ? 1 : 0;
        int swordIdx = hasSword ? 1 : 0;
        if (gold >= visited[0][0][0][0].length) gold = visited[0][0][0][0].length - 1; // prevent OOB
        if (visited[r][c][pickaxeIdx][swordIdx][gold]) return;
        visited[r][c][pickaxeIdx][swordIdx][gold] = true;

        boolean pickedPickaxe = false, pickedSword = false, minedGold = false, gotKey = false, defeatedMonster = false;

        // Pickaxe
        if (map[r][c] == 'X' && !hasPickaxe) {
            hasPickaxe = true;
            pickedPickaxe = true;
            map[r][c] = ' ';
            System.out.println("Pickaxe obtained");
        }
        // Sword
        if (map[r][c] == 'W' && !hasSword) {
            hasSword = true;
            pickedSword = true;
            map[r][c] = ' ';
            System.out.println("Sword obtained");
        }
        // Gold vein
        if (map[r][c] == 'G' && hasPickaxe) {
            gold += 10;
            map[r][c] = ' '; // Remove gold vein
            minedGold = true;
            System.out.println("Gold mined, total gold: " + gold);
        }
        // Monster
        if (map[r][c] == 'M') {
            if (hasSword) {
                gold += 10;
                map[r][c] = ' '; // Remove monster
                defeatedMonster = true;
                System.out.println("Monster defeated, total gold: " + gold);
            } else {
                health -= 100;
                System.out.println("Attacked by monster, health now: " + health);
                if (health <= 0) return;
            }
        }
        // NPC
        if (map[r][c] == 'N') {
            System.out.println("At NPC: gold=" + gold + " hasKey=" + hasKey);
            if (gold >= 30 && !hasKey) {
                hasKey = true;
                gold -= 30;
                System.out.println("Key obtained from NPC");
                gotKey = true;
            }
        }

        for (int d = 0; d < 4; d++) {
            int nr = r + dr[d], nc = c + dc[d];
            if (nr >= 0 && nr < map.length && nc >= 0 && nc < map[0].length &&
                !FileReader2DArray.isWall(map, nr, nc)) {
                if (map[nr][nc] == 'E' && !hasKey)
                    continue;
                char temp = map[nr][nc];
                if (temp != 'E' && temp != 'P' && temp != 'K' && temp != 'L' && temp != 'X'
                    && temp != 'G' && temp != 'N' && temp != 'W' && temp != 'M')
                    map[nr][nc] = '*';
                backtrackMap4(map, visited, nr, nc, steps + 1, hasKey, health, hasPickaxe, hasSword, gold);
                if (temp != 'E' && temp != 'P' && temp != 'K' && temp != 'L' && temp != 'X'
                    && temp != 'G' && temp != 'N' && temp != 'W' && temp != 'M')
                    map[nr][nc] = ' ';
            }
        }
        
        // Undo actions for backtracking
        if (pickedPickaxe) hasPickaxe = false;
        if (pickedPickaxe) map[r][c] = 'X';
        if (pickedSword) hasSword = false;
        if (pickedSword) map[r][c] = 'W';
        if (minedGold) map[r][c] = 'G';
        if (defeatedMonster) map[r][c] = 'M';
        if (gotKey) hasKey = false;
    }

    // Main backtracking for map 5
    static void backtrackMap5(
        char[][] map,
        HashSet<String> visited,
        int r, int c,
        int steps,
        boolean hasKey,
        int health,
        boolean hasPickaxe,
        boolean hasSword,
        int gold,
        char[][] pathMap
        ) {
            visualizer.updateMap(pathMap, tries);
            tries++;
            // Encode state: player position, key, pickaxe, sword, gold, health, gold veins, monsters, log positions
            String state = r + "," + c + "," + hasKey + "," + hasPickaxe + "," + hasSword + "," + gold + "," + health
                + "|" + encodeGoldVeins(map)
                + "|" + encodeMonsters(map)
                + "|" + encodeLogs(map);
            if (visited.contains(state)) return;
            visited.add(state);
            if (health <= 0) return;

            if (map[r][c] == 'E' && hasKey) {
                if (steps < minSteps || (steps == minSteps && health > bestHealth)) {
                    minSteps = steps;
                    // Copy the pathMap as the bestMap
                    bestMap = new char[pathMap.length][pathMap[0].length];
                    for (int i = 0; i < pathMap.length; i++)
                        bestMap[i] = pathMap[i].clone();
                    bestHealth = health;
                    System.out.println("Found exit at (" + r + "," + c + ") in " + steps + " steps!");
                }
                return;
            }

            boolean pickedPickaxe = false, pickedSword = false, minedGold = false, boughtKey = false, defeatedMonster = false;

            // Pickaxe
            if (map[r][c] == 'X' && !hasPickaxe) {
                hasPickaxe = true;
                pickedPickaxe = true;
                map[r][c] = ' ';
                System.out.println("Pickaxe obtained at (" + r + "," + c + ")");
            }
            // Sword
            if (map[r][c] == 'W' && !hasSword) {
                hasSword = true;
                pickedSword = true;
                map[r][c] = ' ';
                System.out.println("Sword obtained at (" + r + "," + c + ")");
            }
            // Gold vein
            if (map[r][c] == 'G') {
                if (hasPickaxe) {
                    gold += 10;
                    map[r][c] = ' '; // Remove gold vein
                    minedGold = true;
                    System.out.println("Gold mined at (" + r + "," + c + "), total gold: " + gold);
                }
            }
            // Monster
            if (map[r][c] == 'M') {
                if (hasSword) {
                    gold += 10;
                    map[r][c] = ' '; // Remove monster
                    defeatedMonster = true;
                    System.out.println("Monster defeated at (" + r + "," + c + "), total gold: " + gold);
                } else {
                    health -= 100;
                    System.out.println("Attacked by monster at (" + r + "," + c + "), health now: " + health);
                    if (health <= 0) return;
                }
            }
            // NPC for buying key (now 50 gold)
            if (map[r][c] == 'N') {
                if (gold >= 50 && !hasKey) {
                    hasKey = true;
                    gold -= 50;
                    boughtKey = true;
                    System.out.println("Key bought from NPC at (" + r + "," + c + "), gold left: " + gold);
                }
            }
            // Reduce health if on lava
            if (map[r][c] == 'L') {
                health -= 50;
                if (health <= 0) return;
            }

            // Mark path for bestMap (only for the current path, not on map)
            boolean marked = false;
            if (pathMap[r][c] == ' ') {
                pathMap[r][c] = '*';
                marked = true;
            }

            for (int d = 0; d < 4; d++) {
                int nr = r + dr[d], nc = c + dc[d];
                if (nr < 0 || nr >= map.length || nc < 0 || nc >= map[0].length) continue;

                // Simulate log movement
                char[][] newMap = copyMap(map);
                moveLogs(newMap);

                char afterMove = newMap[nr][nc];

                // Check if can step
                boolean canStep = false;
                if (afterMove == ' ' || afterMove == 'K' || afterMove == 'E' || afterMove == 'L' || afterMove == 'X' || afterMove == 'G' || afterMove == 'N' || afterMove == 'W' || afterMove == 'M') {
                    canStep = true;
                } else if (afterMove == 'O') {
                    canStep = true; // log on water
                } else if (afterMove == 'A') {
                    canStep = false; // water without log
                } else {
                    canStep = false; // wall or S
                }
                if (!canStep) continue;

                // Don't allow entering 'E' before getting the key
                if (afterMove == 'E' && !hasKey) continue;

                backtrackMap5(newMap, visited, nr, nc, steps + 1, hasKey, health, hasPickaxe, hasSword, gold, pathMap);
            }

            // Undo actions for backtracking
            if (marked) pathMap[r][c] = ' ';
            if (pickedPickaxe) hasPickaxe = false;
            if (pickedPickaxe) map[r][c] = 'X';
            if (pickedSword) hasSword = false;
            if (pickedSword) map[r][c] = 'W';
            if (minedGold) map[r][c] = 'G';
            if (defeatedMonster) map[r][c] = 'M';
            if (boughtKey) {
                hasKey = false;
                gold += 50;
            }
        }

        // Helper to encode monster positions for visited state
    static String encodeMonsters(char[][] map) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == 'M') sb.append(i).append(',').append(j).append(';');
            }
        }
        return sb.toString();
    }

    // Helper to encode gold vein positions for visited state
    static String encodeGoldVeins(char[][] map) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == 'G') sb.append(i).append(',').append(j).append(';');
            }
        }
        return sb.toString();
    }

    // Helper to encode log positions for visited state
    static String encodeLogs(char[][] map) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == 'O') sb.append(i).append(',').append(j).append(';');
            }
        }
        return sb.toString();
    }

    // Move all logs in the map one step to the right on their water line
    static void moveLogs(char[][] map) {
        // Debug: print log positions before move
        // System.out.print("Log positions before move: ");
        // System.out.println(encodeLogs(map));
    
        for (int row = 0; row < map.length; row++) {
            int left = -1, right = -1;
            for (int col = 0; col < map[0].length; col++) {
                if (map[row][col] == 'A' || map[row][col] == 'O') {
                    if (left == -1) left = col;
                    right = col;
                }
            }
            if (left == -1) continue;
            boolean[] hasLog = new boolean[right - left + 1];
            for (int col = left; col <= right; col++) {
                if (map[row][col] == 'O') hasLog[col - left] = true;
            }
            for (int col = left; col <= right; col++) {
                int idx = (col - left - 1 + (right - left + 1)) % (right - left + 1);
                map[row][col] = hasLog[idx] ? 'O' : 'A';
            }
        }
    
        // Debug: print log positions after move
        // System.out.print("Log positions after move: ");
        // System.out.println(encodeLogs(map));
    }

    // Deep copy a 2D char array
    static char[][] copyMap(char[][] map) {
        char[][] newMap = new char[map.length][map[0].length];
        for (int i = 0; i < map.length; i++)
            newMap[i] = map[i].clone();
        return newMap;
    }
}
