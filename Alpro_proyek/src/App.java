import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class App {    
    static int[] dr = { -1, 1, 0, 0 }; // up, down, left, right
    static int[] dc = { 0, 0, -1, 1 };    
    static int tries = 0;
    static int minSteps = Integer.MAX_VALUE;
    static char[][] bestMap = null;
    static boolean found = false;
    static MapVisualizer visualizer;
    static boolean hasSword = false;
    static boolean hasPickaxe = false;
    static int gold = 0;
      // Track the best solution path
    static Path bestPath = null;
    
    // Track the current path during backtracking 
    static Path currentPath = new Path();
    
    // Lists to store paths for each solution
    static List<Path> allExitPaths = new ArrayList<>();
    
    // Path class to track the complete path of a solution
    static class Path {
        List<int[]> positions; // Stores [row, col] for each step
        
        public Path() {
            positions = new ArrayList<>();
        }
        
        public Path(Path other) {
            positions = new ArrayList<>();
            for (int[] pos : other.positions) {
                positions.add(new int[]{pos[0], pos[1]});
            }
        }
        
        public void addPosition(int row, int col) {
            positions.add(new int[]{row, col});
        }
        
        public void removeLastPosition() {
            if (!positions.isEmpty()) {
                positions.remove(positions.size() - 1);
            }
        }
        
        public int size() {
            return positions.size();
        }
        
        // Apply path markers to a map
        public void markPath(char[][] map) {
            // Skip the first position (start) and last position (end)
            for (int i = 1; i < positions.size() - 1; i++) {
                int[] pos = positions.get(i);
                int r = pos[0];
                int c = pos[1];
                if (map[r][c] != 'P' && map[r][c] != 'E' && 
                    map[r][c] != 'K' && map[r][c] != 'L') {
                    map[r][c] = '*';
                }
            }
        }    }
    
    // Lists to store all solutions
    static List<char[][]> allExitMaps = new ArrayList<>();
    static List<Integer> allExitSteps = new ArrayList<>();
    static List<Integer> allExitHealths = new ArrayList<>();


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
            case 6:
                initialHealth = 200;
                mapFile = "Alpro_proyek/src/Z_array6.txt";
                break;
            case 7:
                initialHealth = 200;
                mapFile = "Alpro_proyek/src/Z_array7.txt";
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
        if (mapChoice > 2) {
            visualizer = new MapVisualizer(map, initialHealth); // for map 3-5
            visualizer.setVisible(true);
        } else {
            visualizer = new MapVisualizer(map); // for map 1 and 2
            visualizer.setVisible(true);
        }
        allExitMaps.clear();
        allExitSteps.clear();
        allExitHealths.clear();        // Reset all path tracking variables before starting backtracking
        currentPath = new Path(); // Initialize the currentPath
        bestPath = null;
        allExitPaths.clear();
        
        // Backtracking
        if (mapChoice == 3 || mapChoice == 7) {
            boolean[][] visited = new boolean[map.length][map[0].length];
            backtrackWithHealth(map, visited, start[0], start[1], 0, false, initialHealth);
        } else if (mapChoice == 4) {
            hasSword = false;
            hasPickaxe = false;
            gold = 0;
            bestHealth = initialHealth;
            boolean[][][][][] visited = new boolean[map.length][map[0].length][2][2][201]; // [row][col][pickaxe][sword][gold]
            backtrackMap4(map, visited, start[0], start[1], 0, false, initialHealth, false, false, 0);        } else if (mapChoice == 5) {
            boolean[][] visited = new boolean[map.length][map[0].length];
            bestHealth = initialHealth;
            char[][] pathMap = copyMap(map); // Make a copy for path marking
            backtrackSimple(map, visited, start[0], start[1], 0, false, initialHealth);        } else if (mapChoice == 6) {
            portal1 = findChar(map, '1');
            portal2 = findChar(map, '2');
            boolean[][] visited = new boolean[map.length][map[0].length];
            bestHealth = initialHealth;
            backtrackSimple(map, visited, start[0], start[1], 0, false, initialHealth);} else {
            boolean[][] visited = new boolean[map.length][map[0].length];
            backtrack(map, visited, start[0], start[1], 0, false);

            if (!allExitMaps.isEmpty()) {
                // Instead of showing all solutions, just count them
                System.out.println("Found " + allExitMaps.size() + " solutions that reach the exit.");
                System.out.println("Best solution has " + minSteps + " steps.");
            } else {
                System.out.println("No path found.");
            }
        }

        if (mapChoice > 2) {
            if (!allExitMaps.isEmpty()) {
                // Instead of showing all solutions, just count them
                System.out.println("Found " + allExitMaps.size() + " solutions that reach the exit.");
                System.out.println("Best solution has " + minSteps + " steps with " + bestHealth + " health remaining.");
            } else {
                System.out.println("No path found.");
            }
        }        if (minSteps < Integer.MAX_VALUE) {
            System.out.println("Best path found:");
            System.out.println("Tries: " + tries);
            System.out.println("Steps: " + minSteps);
            if (mapChoice > 2) {
                System.out.println("Remaining Health: " + bestHealth);
            }
            
            // Show the best solution step by step in the GUI
            System.out.println("Displaying best solution step by step in GUI...");            // Ask user for animation speed
            System.out.print("Enter animation speed (1=Slow, 2=Medium, 3=Fast): ");
            int animationSpeed = 2; // Default is medium
            Scanner speedScanner = null;
            try {
                speedScanner = new Scanner(System.in);
                animationSpeed = speedScanner.nextInt();
                if (animationSpeed < 1 || animationSpeed > 3) {
                    System.out.println("Invalid speed, using medium speed.");
                    animationSpeed = 2;
                }
            } catch (Exception e) {
                System.out.println("Invalid input, using medium speed.");
            } finally {
                if (speedScanner != null) {
                    speedScanner.close();
                }
            }
            
            // Find the best solution from allExitMaps
            int bestSolutionIndex = -1;
            
            if (!allExitMaps.isEmpty()) {
                for (int i = 0; i < allExitMaps.size(); i++) {
                    // For maps with health, prioritize best health with minimum steps
                    if (mapChoice > 2) {
                        if ((allExitSteps.get(i) == minSteps) && 
                            (bestSolutionIndex == -1 || allExitHealths.get(i) > allExitHealths.get(bestSolutionIndex))) {
                            bestSolutionIndex = i;
                        }
                    } 
                    // For maps 1-2, just find minimum steps
                    else {
                        if ((allExitSteps.get(i) == minSteps) && bestSolutionIndex == -1) {
                            bestSolutionIndex = i;
                        }
                    }
                }
            }            if (bestSolutionIndex != -1) {
                // Print info about the best solution
                System.out.println("Showing best solution with " + allExitSteps.get(bestSolutionIndex) + 
                    (mapChoice > 2 ? " steps and " + allExitHealths.get(bestSolutionIndex) + " health." : " steps."));
                
                // Check if the solution map has path markers for animation
                char[][] bestSolutionMap = allExitMaps.get(bestSolutionIndex);
                // Variable to count path markers
                int markerCount = 0;
                  // Count path markers in the solution map
                for (int r = 0; r < bestSolutionMap.length; r++) {
                    for (int c = 0; c < bestSolutionMap[0].length; c++) {
                        if (bestSolutionMap[r][c] == '*') {
                            markerCount++;
                        }
                    }
                }
                
                System.out.println("Solution map has " + markerCount + " path markers.");
                int finalHealth = mapChoice > 2 ? allExitHealths.get(bestSolutionIndex) : 0;
                
                // Pass the animation speed
                System.out.println("Animation speed set to: " + 
                    (animationSpeed == 1 ? "Slow" : animationSpeed == 2 ? "Medium" : "Fast"));
                  // Use the exact best solution path for step by step visualization
                showExactBestSolutionStepByStep(bestSolutionMap, mapChoice, finalHealth, animationSpeed);
            } else {
                // Just show the final best solution if we can't reconstruct steps
                int finalHealth = mapChoice > 2 ? bestHealth : 0;
                System.out.println("Cannot reconstruct step-by-step path, showing final solution.");
                visualizer.updateMap(bestMap, tries, finalHealth);
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

        // First clear any * from the map (previous player position)
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == '*') {
                    map[i][j] = (map[i][j] == '*') ? '-' : map[i][j];
                }
            }
        }

        // Mark current player position with *
        char originalCell = map[r][c];
        if (originalCell != 'P' && originalCell != 'E' && originalCell != 'K') {
            map[r][c] = '*';
        }

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
            // Save every map and step that reaches the exit
            char[][] solutionMap = new char[map.length][map[0].length];
            for (int i = 0; i < map.length; i++) {
                solutionMap[i] = map[i].clone();
            }
            allExitMaps.add(solutionMap);
            allExitSteps.add(steps);

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

                char temp = map[nr][nc];
                if (temp != 'E' && temp != 'P' && temp != 'K')
                    map[nr][nc] = '-'; // Mark path with -

                backtrack(map, visited, nr, nc, steps + 1, hasKey);

                if (temp != 'E' && temp != 'P' && temp != 'K')
                    map[nr][nc] = ' '; // Unmark if not correct path
            }
        }

        // Restore original cell content when backtracking
        if (originalCell != 'P' && originalCell != 'E' && originalCell != 'K') {
            map[r][c] = originalCell;
        }

        if (pickedKey)
            hasKey = false; // Backtrack key pickup
        visited[r][c] = false;
    }    static int bestHealth = -1;
      // Current path being explored during backtracking is defined at the class level
    
    // Backtracking for map 3 with health and lava
    static void backtrackWithHealth(char[][] map, boolean[][] visited, int r, int c, int steps, boolean hasKey,
            int health) {
        if (tries >= 10000000) {
            if (minSteps == Integer.MAX_VALUE) {
                System.out.println("No path found (try limit reached).");
            }
            return;
        }
        tries++;
        
        // Add current position to path (or update if we're at this depth already)
        if (currentPath.positions.size() <= steps) {
            currentPath.addPosition(r, c);
        } else if (currentPath.positions.size() > steps) {
            // We've backtracked, adjust the path
            while (currentPath.positions.size() > steps + 1) {
                currentPath.removeLastPosition();
            }
            if (currentPath.positions.size() == steps + 1) {
                // Replace position at current step
                currentPath.positions.set(steps, new int[]{r, c});
            }
        }

        // First clear any * from the map (previous player position)
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == '*') {
                    map[i][j] = (map[i][j] == '*') ? '-' : map[i][j];
                }
            }
        }

        // Mark current player position with *
        char originalCell = map[r][c];
        if (originalCell != 'P' && originalCell != 'E' && originalCell != 'K' && originalCell != 'L') {
            map[r][c] = '*';
        }

        visualizer.updateMap(map, tries, health);

        if (health <= 0) {
            // Restore original cell content when backtracking
            if (originalCell != 'P' && originalCell != 'E' && originalCell != 'K' && originalCell != 'L') {
                map[r][c] = originalCell;
            }
            return;
        }        if (map[r][c] == 'E' && hasKey) {
            // Save a copy of the current path
            Path solutionPath = new Path(currentPath);
            
            // Store solution in our lists
            allExitPaths.add(solutionPath);
            allExitSteps.add(steps);
            allExitHealths.add(health);
            
            // Check if this is the best solution so far
            if (steps < minSteps || (steps == minSteps && health > bestHealth)) {
                minSteps = steps;
                bestHealth = health;
                
                // Store the best path
                bestPath = solutionPath;
                
                // Create best map to visualize
                bestMap = new char[map.length][map[0].length];
                for (int i = 0; i < map.length; i++) {
                    bestMap[i] = map[i].clone();
                }
                
                // Mark the path on the map
                for (int i = 1; i < solutionPath.positions.size() - 1; i++) {
                    int[] pos = solutionPath.positions.get(i);
                    if (bestMap[pos[0]][pos[1]] != 'P' && 
                        bestMap[pos[0]][pos[1]] != 'E' && 
                        bestMap[pos[0]][pos[1]] != 'K' && 
                        bestMap[pos[0]][pos[1]] != 'L') {
                        bestMap[pos[0]][pos[1]] = '*';
                    }
                }
            }
            
            // Create a solution map from the clean map
            char[][] solutionMap = new char[map.length][map[0].length];
            for (int i = 0; i < map.length; i++) {
                solutionMap[i] = map[i].clone();
            }
            
            // Mark the path on the solution map
            solutionPath.markPath(solutionMap);
            
            // Store the solution map
            allExitMaps.add(solutionMap);
            
            // Show the current best solution
            visualizer.updateMap(bestMap, tries, health);
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
                    map[nr][nc] = '-';

                backtrackWithHealth(map, visited, nr, nc, steps + 1, hasKey, nextHealth);

                if (temp != 'E' && temp != 'P' && temp != 'K' && temp != 'L')
                    map[nr][nc] = ' ';
            }
        }        // Restore original cell content when backtracking
        if (originalCell != 'P' && originalCell != 'E' && originalCell != 'K' && originalCell != 'L') {
            map[r][c] = originalCell;
        }

        // Update the path for backtracking
        if (currentPath.positions.size() > steps + 1) {
            currentPath.removeLastPosition();
        }
        
        if (pickedKey)
            hasKey = false;
        visited[r][c] = false;
    }    // Field declarations moved to class level

    static void backtrackMap4(
            char[][] map,
            boolean[][][][][] visited, // [row][col][pickaxe][sword][gold]
            int r, int c, int steps, boolean hasKey,
            int health, boolean hasPickaxe, boolean hasSword, int gold) {
        // First clear any * from the map (previous player position)
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == '*') {
                    map[i][j] = '-';
                }
            }
        }

        // Mark current player position with *
        char originalCell = map[r][c];
        if (originalCell != 'P' && originalCell != 'E' && originalCell != 'K' && originalCell != 'L' &&
                originalCell != 'X' && originalCell != 'G' && originalCell != 'N' && originalCell != 'W' &&
                originalCell != 'M') {
            map[r][c] = '*';
        }

        visualizer.updateMap(map, tries, health);
        tries++;

        if (health <= 0) {
            // Restore original cell if needed
            if (originalCell != 'P' && originalCell != 'E' && originalCell != 'K' && originalCell != 'L' &&
                    originalCell != 'X' && originalCell != 'G' && originalCell != 'N' && originalCell != 'W' &&
                    originalCell != 'M' && map[r][c] == '*') {
                map[r][c] = originalCell;
            }
            return;
        }

        if (map[r][c] == 'E' && hasKey) {
            if (steps < minSteps || (steps == minSteps && health > bestHealth)) {
                minSteps = steps;
                bestHealth = health;
                bestMap = new char[map.length][map[0].length];
                for (int i = 0; i < map.length; i++)
                    bestMap[i] = map[i].clone();
            }
            char[][] solutionMap = new char[map.length][map[0].length];
            for (int i = 0; i < map.length; i++) {
                solutionMap[i] = map[i].clone();
            }
            allExitMaps.add(solutionMap);
            allExitSteps.add(steps);
            allExitHealths.add(health);

            visualizer.updateMap(bestMap, tries, health);
            return;
        }

        int pickaxeIdx = hasPickaxe ? 1 : 0;
        int swordIdx = hasSword ? 1 : 0;
        if (gold >= visited[0][0][0][0].length)
            gold = visited[0][0][0][0].length - 1; // prevent OOB
        if (visited[r][c][pickaxeIdx][swordIdx][gold])
            return;
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
                if (health <= 0)
                    return;
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
                    map[nr][nc] = '-';
                backtrackMap4(map, visited, nr, nc, steps + 1, hasKey, health, hasPickaxe, hasSword, gold);
                if (temp != 'E' && temp != 'P' && temp != 'K' && temp != 'L' && temp != 'X'
                        && temp != 'G' && temp != 'N' && temp != 'W' && temp != 'M')
                    map[nr][nc] = ' ';
            }
        }

        // Restore original cell content when backtracking
        if (originalCell != 'P' && originalCell != 'E' && originalCell != 'K' && originalCell != 'L' &&
                originalCell != 'X' && originalCell != 'G' && originalCell != 'N' && originalCell != 'W' &&
                originalCell != 'M' && map[r][c] == '*') {
            map[r][c] = originalCell;
        }

        // Undo actions for backtracking
        if (pickedPickaxe)
            hasPickaxe = false;
        if (pickedPickaxe)
            map[r][c] = 'X';
        if (pickedSword)
            hasSword = false;
        if (pickedSword)
            map[r][c] = 'W';
        if (minedGold)
            map[r][c] = 'G';
        if (defeatedMonster)
            map[r][c] = 'M';
        if (gotKey)
            hasKey = false;
    }

    // Main backtracking for map 5    static void backtrackMap5(
            char[][] map,
            ArrayList<String> visited,
            int r, int c,
            int steps,
            boolean hasKey,
            int health,
            boolean hasPickaxe,
            boolean hasSword,
            int gold,
            char[][] pathMap) {
                
        // Add current position to path (or update if we're at this depth already)
        if (currentPath.positions.size() <= steps) {
            currentPath.addPosition(r, c);
        } else if (currentPath.positions.size() > steps) {
            // We've backtracked, adjust the path
            while (currentPath.positions.size() > steps + 1) {
                currentPath.removeLastPosition();
            }
            if (currentPath.positions.size() == steps + 1) {
                // Replace position at current step
                currentPath.positions.set(steps, new int[]{r, c});
            }
        }
        
        // First clear any * from pathMap (previous player position)
        for (int i = 0; i < pathMap.length; i++) {
            for (int j = 0; j < pathMap[0].length; j++) {
                if (pathMap[i][j] == '*') {
                    pathMap[i][j] = '-';
                }
            }
        }

        // Mark current player position with *
        char originalCell = pathMap[r][c];
        boolean canBeMarked = (originalCell == ' ' || originalCell == '-');
        if (canBeMarked) {
            pathMap[r][c] = '*';
        }

        visualizer.updateMap(pathMap, tries);
        tries++;

        // Encode state: player position, key, pickaxe, sword, gold, health, gold veins,
        // monsters, log positions
        String state = r + "," + c + "," + hasKey + "," + hasPickaxe + "," + hasSword + "," + gold + "," + health
                + "|" + encodeGoldVeins(map)
                + "|" + encodeMonsters(map)
                + "|" + encodeLogs(map);
        if (visited.contains(state)) {
            // Restore original cell when backtracking
            if (canBeMarked) {
                pathMap[r][c] = originalCell;
            }
            
            // Update the path for backtracking
            if (currentPath.positions.size() > steps + 1) {
                currentPath.removeLastPosition();
            }
            
            return;
        }
        visited.add(state);

        if (health <= 0) {
            // Restore original cell when backtracking
            if (canBeMarked) {
                pathMap[r][c] = originalCell;
            }
            
            // Update the path for backtracking
            if (currentPath.positions.size() > steps + 1) {
                currentPath.removeLastPosition();
            }
            
            return;
        }

        if (map[r][c] == 'E' && hasKey) {
            // Save a copy of the current path
            Path solutionPath = new Path(currentPath);
            
            // Store solution in our lists
            allExitPaths.add(solutionPath);
            
            if (steps < minSteps || (steps == minSteps && health > bestHealth)) {
                minSteps = steps;
                // Copy the pathMap as the bestMap
                bestMap = new char[pathMap.length][pathMap[0].length];
                for (int i = 0; i < pathMap.length; i++)
                    bestMap[i] = pathMap[i].clone();
                bestHealth = health;
                
                // Store the best path
                bestPath = solutionPath;
                
                System.out.println("Found exit at (" + r + "," + c + ") in " + steps + " steps!");
            }
            char[][] solutionMap = new char[pathMap.length][pathMap[0].length];
            for (int i = 0; i < pathMap.length; i++) {
                solutionMap[i] = pathMap[i].clone();
            }
            allExitMaps.add(solutionMap);
            allExitSteps.add(steps);
            allExitHealths.add(health);

            visualizer.updateMap(bestMap, tries, health);
            // Restore original cell when backtracking
            if (canBeMarked) {
                pathMap[r][c] = originalCell;
            }
            
            // Update the path for backtracking
            if (currentPath.positions.size() > steps + 1) {
                currentPath.removeLastPosition();
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
                    map[nr][nc] = '-';

                backtrackMap6(map, visited, nr, nc, steps + 1, hasKey, nextHealth, hasPickaxe, hasSword, gold, pathMap);

                if (temp != 'E' && temp != 'P' && temp != 'K' && temp != 'L')
                    map[nr][nc] = ' ';
            }
        }        // Restore original cell content when backtracking
        if (originalCell != 'P' && originalCell != 'E' && originalCell != 'K' && originalCell != 'L') {
            map[r][c] = originalCell;
        }

        // Update the path for backtracking
        if (currentPath.positions.size() > steps + 1) {
            currentPath.removeLastPosition();
        }
        
        if (pickedKey)
            hasKey = false;
        visited[r][c] = false;
    }

    static int[] portal1 = null;
    static int[] portal2 = null;
    
    // Deep copy a 2D char array
    static char[][] copyMap(char[][] map) {
        char[][] newMap = new char[map.length][map[0].length];
        for (int i = 0; i < map.length; i++)
            newMap[i] = map[i].clone();
        return newMap;    }    
    
    static void backtrackMap6(char[][] map, ArrayList<String> visited, int r, int c, int steps,
            boolean hasKey, int health, boolean localHasPickaxe, boolean localHasSword, int localGold,
            char[][] pathMap) {
        // First clear any * from pathMap (previous player position)
        for (int i = 0; i < pathMap.length; i++) {
            for (int j = 0; j < pathMap[0].length; j++) {
                if (pathMap[i][j] == '*') {
                    pathMap[i][j] = '-';
                }
            }
        }

        // Mark current player position with *
        char originalCell = pathMap[r][c];
        boolean canBeMarked = (originalCell == ' ' || originalCell == '-');
        if (canBeMarked) {
            pathMap[r][c] = '*';
        }

        visualizer.updateMap(pathMap, tries);
        tries++;

        // Encode state: player position, key, pickaxe, sword, gold, health, gold veins,
        // monsters, log positions
        String state = r + "," + c + "," + hasKey + "," + hasPickaxe + "," + hasSword + "," + gold + "," + health
                + "|" + encodeGoldVeins(map)
                + "|" + encodeMonsters(map)
                + "|" + encodeLogs(map);
        if (visited.contains(state)) {
            // Restore original cell when backtracking
            if (canBeMarked) {
                pathMap[r][c] = originalCell;
            }
            return;
        }
        visited.add(state);

        if (health <= 0) {
            // Restore original cell when backtracking
            if (canBeMarked) {
                pathMap[r][c] = originalCell;
            }
            return;
        }

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
            char[][] solutionMap = new char[pathMap.length][pathMap[0].length];
            for (int i = 0; i < pathMap.length; i++) {
                solutionMap[i] = pathMap[i].clone();
            }
            allExitMaps.add(solutionMap);
            allExitSteps.add(steps);
            allExitHealths.add(health);

            visualizer.updateMap(bestMap, tries, health);
            // Restore original cell when backtracking
            if (canBeMarked) {
                pathMap[r][c] = originalCell;
            }
            return;
        }

        boolean pickedPickaxe = false, pickedSword = false, minedGold = false, boughtKey = false,
                defeatedMonster = false;

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
                if (health <= 0) {
                    // Restore original cell when backtracking
                    if (canBeMarked) {
                        pathMap[r][c] = originalCell;
                    }
                    return;
                }
            }
        }
        // NPC for buying key (now 40 gold)
        if (map[r][c] == 'N') {
            if (gold >= 40 && !hasKey) {
                hasKey = true;
                gold -= 40;
                boughtKey = true;
                System.out.println("Key bought from NPC at (" + r + "," + c + "), gold left: " + gold);
            }
        }
        // Reduce health if on lava
        if (map[r][c] == 'L') {
            health -= 50;
            if (health <= 0)
                return;
        }

        // Portal logic: teleport if on '1' or '2'
        if (map[r][c] == '1' && portal2 != null) {
            int destRow = portal2[0] - 1;
            int destCol = portal2[1];
            // Only teleport if destination is within bounds and not a wall
            if (destRow >= 0 && !FileReader2DArray.isWall(map, destRow, destCol)) {
                System.out.println("Portal used: from (1) at (" + r + "," + c + ") to above (2) at (" + destRow + ","
                        + destCol + ")");
                backtrackMap6(map, visited, destRow, destCol, steps + 1, hasKey, health, hasPickaxe, hasSword, gold,
                        pathMap);
                return; // Do not continue normal movement from here
            }
        }

        if (map[r][c] == '2' && portal1 != null) {
            int destRow = portal1[0] - 1;
            int destCol = portal1[1];
            if (destRow >= 0 && !FileReader2DArray.isWall(map, destRow, destCol)) {
                System.out.println("Portal used: from (2) at (" + r + "," + c + ") to above (1) at (" + destRow + ","
                        + destCol + ")");
                backtrackMap6(map, visited, destRow, destCol, steps + 1, hasKey, health, hasPickaxe, hasSword, gold,
                        pathMap);
                return;
            }
        }

        // Mark path for bestMap (only for the current path, not on map)
        boolean marked = false;
        if (pathMap[r][c] == ' ' && map[r][c] != '1' && map[r][c] != '2') {
            pathMap[r][c] = '*';
            marked = true;
        }

        for (int d = 0; d < 4; d++) {
            int nr = r + dr[d], nc = c + dc[d];
            if (nr < 0 || nr >= map.length || nc < 0 || nc >= map[0].length)
                continue;

            // Simulate log movement
            char[][] newMap = copyMap(map);
            
            moveLogs(newMap);

            char afterMove = newMap[nr][nc];

            // Check if can step
            boolean canStep = false;
            if (afterMove == ' ' || afterMove == 'K' || afterMove == 'E' || afterMove == 'L' || afterMove == 'X'
                    || afterMove == 'G' || afterMove == 'N' || afterMove == 'W' || afterMove == 'M' || afterMove == '1'
                    || afterMove == '2') {
                canStep = true;
            } else if (afterMove == 'O') {
                canStep = true; // log on water
            } else if (afterMove == 'A') {
                canStep = false; // water without log
            } else {
                canStep = false; // wall or S
            }
            if (!canStep)
                continue;

            // Don't allow entering 'E' before getting the key
            if (afterMove == 'E' && !hasKey)
                continue;

            backtrackMap6(newMap, visited, nr, nc, steps + 1, hasKey, health, hasPickaxe, hasSword, gold, pathMap);
        }

        // Undo actions for backtracking
        if (marked)
            pathMap[r][c] = ' ';
        if (pickedPickaxe)
            hasPickaxe = false;
        if (pickedPickaxe)
            map[r][c] = 'X';
        if (pickedSword)
            hasSword = false;
        if (pickedSword)
            map[r][c] = 'W';
        if (minedGold)
            map[r][c] = 'G';
        if (defeatedMonster)
            map[r][c] = 'M';
        if (boughtKey) {
            hasKey = false;
            gold += 40; // Corrected from 50 to 40
        }
    }    // Method to show the best solution step by step in the GUI    // This method is deprecated and will be removed
    // It has been replaced by showExactBestSolutionStepByStep
    private static void showBestSolutionStepByStep(char[][] solutionMap, int mapChoice, int finalHealth, int speedSetting) {
        // Delegate to the new implementation
        showExactBestSolutionStepByStep(solutionMap, mapChoice, finalHealth, speedSetting);    }
        
        // Create a working map that we'll update as we go
        char[][] workingMap = copyMap(initialMap);
        
        // Get the exact path positions
        List<int[]> pathPositions = bestPath.positions;
        System.out.println("Starting step-by-step visualization with " + pathPositions.size() + " steps");
        
        // For maps with very large numbers of steps, select a subset for smoother animation
        if ((mapChoice == 5 || mapChoice == 6) && pathPositions.size() > 50) {
            List<int[]> selectedPositions = new ArrayList<>();
            int step = Math.max(1, pathPositions.size() / 40); // Target about 40 steps
            for (int i = 0; i < pathPositions.size(); i += step) {
                selectedPositions.add(pathPositions.get(i));
            }
            // Always include the last position
            if (!selectedPositions.contains(pathPositions.get(pathPositions.size() - 1))) {
                selectedPositions.add(pathPositions.get(pathPositions.size() - 1));
            }
            pathPositions = selectedPositions;
            System.out.println("Selected " + pathPositions.size() + " steps out of " + bestPath.positions.size() +
                              " for smoother visualization");
        }
        
        // First show the initial state
        visualizer.updateMap(workingMap, tries, mapChoice > 2 ? 200 : 0);
        try {
            Thread.sleep(INITIAL_DELAY_MS); // Extra time at start
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        boolean hasKey = false;
        boolean hasSword = false;
        boolean hasPickaxe = false;
        int gold = 0;
        
        // Go through each step in the path
        for (int i = 0; i < pathPositions.size(); i++) {
            int[] pos = pathPositions.get(i);
            int r = pos[0];
            int c = pos[1];
            
            // Handle special cells and update player status
            char cellType = initialMap[r][c];
            
            // Move logs for each step if this map has logs
            if (mapChoice == 5 || mapChoice == 6) {
                moveLogs(workingMap);
                // After moving logs, check if the player is standing on water
                if (workingMap[r][c] == 'A') {
                    // If water without log, player would die, so look for a nearby log
                    boolean logFound = false;
                    for (int dr = -1; dr <= 1; dr++) {
                        for (int dc = -1; dc <= 1; dc++) {
                            int nr = r + dr;
                            int nc = c + dc;
                            if (nr >= 0 && nr < workingMap.length && 
                                nc >= 0 && nc < workingMap[0].length && 
                                workingMap[nr][nc] == 'O') {
                                r = nr;
                                c = nc;
                                logFound = true;
                                break;
                            }
                        }
                        if (logFound) break;
                    }
                }
            }
            
            // Special cell handling
            switch (cellType) {
                case 'K':
                    hasKey = true;
                    workingMap[r][c] = ' '; // Remove key after collection
                    break;
                case 'W':
                    hasSword = true;
                    workingMap[r][c] = ' '; // Remove sword after collection
                    break;
                case 'X':
                    hasPickaxe = true;
                    workingMap[r][c] = ' '; // Remove pickaxe after collection
                    break;
                case 'G':
                    gold += 10; // Add 10 gold
                    workingMap[r][c] = ' '; // Remove gold after mining
                    break;
                case 'M':
                    if (hasSword) {
                        workingMap[r][c] = ' '; // Remove monster after defeating
                    }
                    break;
                case 'N':
                    if (gold >= 40 && !hasKey) {
                        hasKey = true;
                        gold -= 40;
                    }
                    break;
            }
            
            // First, clear the previous player position
            for (int row = 0; row < workingMap.length; row++) {
                for (int col = 0; col < workingMap[0].length; col++) {
                    if (workingMap[row][col] == 'P' && (row != findChar(initialMap, 'P')[0] || col != findChar(initialMap, 'P')[1])) {
                        workingMap[row][col] = ' '; // Clear old player position if it's not the start
                    }
                }
            }
            
            // Mark current player position
            if (workingMap[r][c] != 'E') {
                char originalCell = workingMap[r][c];
                workingMap[r][c] = 'P';
                
                // Special case for water/log
                if (originalCell == 'O') {
                    System.out.println("Player is on a log at step " + i);
                }
            }
            
            // Update health (based on current position in path)
            int currentHealth = mapChoice > 2 ? 200 : 0;
            if (mapChoice > 2) {
                if (i == pathPositions.size() - 1) {
                    currentHealth = finalHealth; // Set to final health on last step
                } else {
                    // Linear interpolation between 200 and finalHealth
                    currentHealth = 200 - ((200 - finalHealth) * i / (pathPositions.size() - 1));
                }
            }
              
            // Show current state in GUI and ensure it's visible
            visualizer.updateMap(workingMap, tries + i, currentHealth);
            
            // Force repaint to ensure the update is visible
            visualizer.forceRepaint();
            
            // Print debug info for each step
            System.out.println("Step " + (i+1) + "/" + pathPositions.size() + 
                " - Position: (" + r + "," + c + ") - Waiting " + STEP_DELAY_MS + "ms");
            
            // Delay between steps
            try {
                Thread.sleep(STEP_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // At the end, show the full solution map
        visualizer.updateMap(solutionMap, tries, finalHealth);
        System.out.println("Step-by-step visualization complete.");
    }
    
    // Helper method to check if a position is already in the list
    private static boolean containsPosition(List<int[]> positions, int row, int col) {
        for (int[] pos : positions) {
            if (pos[0] == row && pos[1] == col) {
                return true;
            }
        }
        return false;
    }
    
    // Helper method to add a point to the path if it's valid (not a wall or special obstacle)
    private static void addValidPathPoint(char[][] map, List<int[]> path, int r, int c, int[] startPos, int[] endPos) {
        // Skip if out of bounds
        if (r < 0 || r >= map.length || c < 0 || c >= map[0].length) {
            return;
        }
        
        // Skip walls and special obstacles
        if (map[r][c] == '#' || map[r][c] == 'S') {
            return;
        }
        
        // Skip if it's start or end and we already have it
        if ((r == startPos[0] && c == startPos[1]) || (r == endPos[0] && c == endPos[1])) {
            for (int[] pos : path) {
                if ((pos[0] == startPos[0] && pos[1] == startPos[1]) || 
                    (pos[0] == endPos[0] && pos[1] == endPos[1])) {
                    return;
                }
            }
        }
        
        // Skip if this position is already in the path
        for (int[] pos : path) {
            if (pos[0] == r && pos[1] == c) {
                return;
            }
        }
        
        // Add the position to the path
        path.add(new int[]{r, c});
    }
    
    // Helper method to move logs in water (for maps 5 and 6)
    static void moveLogs(char[][] map) {
        // Debug: print log positions before move
        // System.out.print("Log positions before move: ");
        // System.out.println(encodeLogs(map));

        for (int row = 0; row < map.length; row++) {
            int left = -1, right = -1;
            for (int col = 0; col < map[0].length; col++) {
                if (map[row][col] == 'A' || map[row][col] == 'O') {
                    if (left == -1)
                        left = col;
                    right = col;
                }
            }
            if (left == -1)
                continue;
            boolean[] hasLog = new boolean[right - left + 1];
            for (int col = left; col <= right; col++) {
                if (map[row][col] == 'O')
                    hasLog[col - left] = true;
            }
            for (int col = left; col <= right; col++) {
                int idx = (col - left - 1 + (right - left + 1)) % (right - left + 1);
                map[row][col] = hasLog[idx] ? 'O' : 'A';
            }
        }
        
        // Debug: print log positions after move
        // System.out.print("Log positions after move: ");
        // System.out.println(encodeLogs(map));
        
        // Add visualization update to see the logs move
        if (visualizer != null) {
            visualizer.updateMap(map, tries);
        }
    }
    
    // Helper method to encode log positions for debugging
    static String encodeLogs(char[][] map) {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[0].length; col++) {
                if (map[row][col] == 'O') {
                    sb.append(row).append(",").append(col).append(";");
                }
            }
        }
        return sb.toString();
    }
    
    // Helper method to encode gold vein positions for debugging
    static String encodeGoldVeins(char[][] map) {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[0].length; col++) {
                if (map[row][col] == 'G') {
                    sb.append(row).append(",").append(col).append(";");
                }
            }
        }
        return sb.toString();
    }
      // Helper method to encode monster positions for debugging
    static String encodeMonsters(char[][] map) {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[0].length; col++) {
                if (map[row][col] == 'M') {
                    sb.append(row).append(",").append(col).append(";");
                }
            }
        }
        return sb.toString();
    }
    
    // Helper method to create intermediate path points between two positions
    private static void createIntermediatePoints(List<int[]> path, int[] start, int[] end, int numPoints, char[][] map) {
        // Use A* like approach to create a reasonable path between two points
        // This is a simplified pathfinding to create good intermediate points
        
        // Calculate the total steps needed (Manhattan distance)
        int dx = Math.abs(end[0] - start[0]);
        int dy = Math.abs(end[1] - start[1]);
        int totalSteps = dx + dy;
        
        // Ensure we have a minimum number of points
        numPoints = Math.max(numPoints, totalSteps);
        
        // Create a queue of points to try, ordered by distance to target
        PriorityQueue<int[]> queue = new PriorityQueue<>((a, b) -> {
            int distA = Math.abs(a[0] - end[0]) + Math.abs(a[1] - end[1]);
            int distB = Math.abs(b[0] - end[0]) + Math.abs(b[1] - end[1]);
            return Integer.compare(distA, distB);
        });
        
        // Start at the start position
        queue.add(start);
          // Keep track of visited positions
        ArrayList<String> visitedPositions = new ArrayList<>();
        visitedPositions.add(start[0] + "," + start[1]);
        
        // Store the path
        List<int[]> tempPath = new ArrayList<>();
        tempPath.add(start);
        
        while (!queue.isEmpty() && tempPath.size() < numPoints) {
            int[] current = queue.poll();
            
            // If we reached the end, we're done
            if (current[0] == end[0] && current[1] == end[1]) {
                break;
            }
            
            // Try all four directions
            for (int d = 0; d < 4; d++) {
                int nr = current[0] + dr[d];
                int nc = current[1] + dc[d];
                
                // Check bounds
                if (nr < 0 || nr >= map.length || nc < 0 || nc >= map[0].length) {
                    continue;
                }
                
                // Skip walls
                if (map[nr][nc] == '#' || map[nr][nc] == 'S') {
                    continue;
                }
                
                // Skip visited positions
                String key = nr + "," + nc;
                if (visitedPositions.contains(key)) {
                    continue;
                }
                
                // Mark as visited
                visitedPositions.add(key);
                
                // Add to queue
                queue.add(new int[]{nr, nc});
                
                // Add to path
                tempPath.add(new int[]{nr, nc});
                
                // Only add one point per iteration to create a nice path
                break;
            }
        }
        
        // If we didn't reach the end, add it
        if (tempPath.size() > 0 && (tempPath.get(tempPath.size() - 1)[0] != end[0] || 
                                   tempPath.get(tempPath.size() - 1)[1] != end[1])) {
            tempPath.add(end);
        }
        
        // Select a reasonable number of points from our path
        int stepSize = Math.max(1, tempPath.size() / numPoints);
        for (int i = 0; i < tempPath.size(); i += stepSize) {
            // Skip start position as it's already in the path
            if (i == 0) continue;
            
            path.add(tempPath.get(i));
        }
        
        System.out.println("Created " + (path.size() - 1) + " intermediate points");
    }
}



