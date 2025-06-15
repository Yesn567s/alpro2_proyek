import java.util.Scanner;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;

public class App {
    static int[] dr = { -1, 1, 0, 0 }; // up, down, left, right
    static int[] dc = { 0, 0, -1, 1 };    static int tries = 0;
    static int minSteps = Integer.MAX_VALUE;
    static char[][] bestMap = null;
    static boolean found = false;
    static MapVisualizer visualizer;
    static List<char[][]> allExitMaps = new ArrayList<>();
    static List<Integer> allExitSteps = new ArrayList<>();
    static List<Integer> allExitHealths = new ArrayList<>();
    static boolean hasSword = false;
    static boolean hasPickaxe = false;
    static int gold = 0;
    
    // Define a class to represent movement steps for the solution
    static class Movement {
        int row;
        int col;
        int direction; // 0=up, 1=down, 2=left, 3=right
        char action; // 'M'=move, 'K'=get key, 'X'=get pickaxe, 'W'=get sword, etc.
        
        public Movement(int row, int col, int direction, char action) {
            this.row = row;
            this.col = col;
            this.direction = direction;
            this.action = action;
        }
        
        @Override
        public String toString() {
            String dirName = "";
            switch (direction) {
                case 0: dirName = "UP"; break;
                case 1: dirName = "DOWN"; break;
                case 2: dirName = "LEFT"; break;
                case 3: dirName = "RIGHT"; break;
                default: dirName = "NONE"; break;
            }
            return "Move " + dirName + " to (" + row + "," + col + ") - " + getActionName();
        }
        
        private String getActionName() {
            switch (action) {
                case 'K': return "Got key";
                case 'X': return "Got pickaxe";
                case 'W': return "Got sword";
                case 'G': return "Mined gold";
                case 'M': return "Defeated monster";
                case 'N': return "Bought key";
                case 'L': return "Walked on lava";
                case 'O': return "Stepped on log";
                case 'E': return "Reached exit";
                case '1': return "Used portal 1";
                case '2': return "Used portal 2";
                default: return "Moved to empty space";
            }
        }
    }
    
    // ArrayList to store best solution movements
    static ArrayList<Movement> bestSolutionMoves = new ArrayList<>();
    // Temporary list to track current path's movements
    static ArrayList<Movement> currentMoves = new ArrayList<>();
    
    // Create a deep copy of the ArrayList of movements
    static ArrayList<Movement> copyMovementList(ArrayList<Movement> original) {
        ArrayList<Movement> copy = new ArrayList<>();
        for (Movement move : original) {
            copy.add(new Movement(move.row, move.col, move.direction, move.action));
        }
        return copy;
    }    public static void main(String[] args) throws Exception {
        // Declare variables outside try block so they're accessible throughout the method
        int mapChoice = 0;
        Scanner scInt = null;
        String mapFile = "";
        int initialHealth = 0;
        
        try {
            scInt = new Scanner(System.in);
            System.out.print("Select map to use: ");
            mapChoice = scInt.nextInt();
            
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
                break;            default:                System.out.println("Invalid map selection.");
                if (scInt != null) scInt.close();
                return;
        }
        } catch (Exception e) {
            System.out.println("Error reading map selection: " + e.getMessage());
            if (scInt != null) scInt.close();
            return;
        }

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
        allExitHealths.clear();

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
            backtrackMap4(map, visited, start[0], start[1], 0, false, initialHealth, false, false, 0);
        } else if (mapChoice == 5) {
            HashSet<String> visited = new HashSet<>();
            bestHealth = initialHealth;
            char[][] pathMap = copyMap(map); // Make a copy for path marking
            backtrackMap5(map, visited, start[0], start[1], 0, false, initialHealth, false, false, 0, pathMap);
        } else if (mapChoice == 6) {
            portal1 = findChar(map, '1');
            portal2 = findChar(map, '2');
            HashSet<String> visited = new HashSet<>();
            bestHealth = initialHealth;
            char[][] pathMap = copyMap(map); // Make a copy for path marking
            backtrackMap6(map, visited, start[0], start[1], 0, false, initialHealth, false, false, 0, pathMap);
        } else {
            boolean[][] visited = new boolean[map.length][map[0].length];
            backtrack(map, visited, start[0], start[1], 0, false);

            // Print all solutions that reach the exit
            if (!allExitMaps.isEmpty()) {
                for (int i = 0; i < allExitMaps.size(); i++) {
                    visualizer.updateMap(allExitMaps.get(i), tries);
                    System.out.println("Showing solution #" + (i + 1) + " (Steps: " + allExitSteps.get(i) + ")");
                    try {
                        Thread.sleep(1000); // delay
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                visualizer.updateMap(bestMap, tries);
                System.out.println("All solutions that reach the exit:");
                for (int i = 0; i < allExitMaps.size(); i++) {
                    System.out.println("Solution #" + (i + 1) + " (Steps: " + allExitSteps.get(i) + "):");
                    FileReader2DArray.print2DCharMap(allExitMaps.get(i));
                    System.out.println();
                }
            } else {
                System.out.println("No path found.");
            }
        }

        if(mapChoice > 2) {
            // Print all solutions that reach the exit
            if (!allExitMaps.isEmpty()) {
                for (int i = 0; i < allExitMaps.size(); i++) {
                    visualizer.updateMap(allExitMaps.get(i), tries, allExitHealths.get(i));
                    System.out.println("Showing solution #" + (i + 1) + " (Steps: " + allExitSteps.get(i) + ", Health: " + allExitHealths.get(i) + ")");
                    try {
                        Thread.sleep(1000); // delay
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                visualizer.updateMap(bestMap, tries);
                System.out.println("All solutions that reach the exit:");
                for (int i = 0; i < allExitMaps.size(); i++) {
                    System.out.println("Solution #" + (i + 1) + " (Steps: " + allExitSteps.get(i) + ", Health: " + allExitHealths.get(i) + "):");
                    FileReader2DArray.print2DCharMap(allExitMaps.get(i));
                    System.out.println();
                }
            } else {
                System.out.println("No path found.");
            }
        }

        if (minSteps < Integer.MAX_VALUE) {
            System.out.println("Path found:");            FileReader2DArray.print2DCharMap(bestMap);
            System.out.println("Tries: " + tries);
            System.out.println("Steps: " + minSteps);
            if (mapChoice == 3 || mapChoice == 4 || mapChoice == 5 || mapChoice == 6 || mapChoice == 7) {
                visualizer.updateMap(bestMap, tries, bestHealth);
                System.out.println("Remaining Health: " + bestHealth);
            } else {
                visualizer.updateMap(bestMap, tries);
            }
            
            // Display step-by-step solution summary
            System.out.println("\nStep-by-Step Solution:");
            System.out.println("Total steps: " + bestSolutionMoves.size());
            
            // Announce step-by-step visualization without requiring user input
            System.out.println("\nStarting step-by-step visualization in 2 seconds...");
            try {
                // Just wait a bit before proceeding with visualization
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            try {
                // Reset the map to initial state
                char[][] initialMap = FileReader2DArray.read2DCharMapFromFile(mapFile);
                int currentHealth = initialHealth;
                
                // Validate solution before visualization
                if (bestSolutionMoves == null || bestSolutionMoves.isEmpty()) {
                    System.out.println("Error: No solution moves recorded. Cannot visualize steps.");
                    return;
                }
                
                // Create a copy of the initial map for visualization
                char[][] stepMap = copyMap(initialMap);
                
                // Print solution information
                System.out.println("Showing step-by-step solution with " + bestSolutionMoves.size() + " steps...");
                System.out.println("Best solution steps: " + minSteps);
                
                // Find the Player starting position
                int[] startPos = findChar(initialMap, 'P');
                if (startPos == null) {
                    System.out.println("Error: Could not find player starting position.");
                    return;
                }
                
                int playerRow = startPos[0];
                int playerCol = startPos[1];
                
                // Track original map
                char[][] originalMapState = copyMap(initialMap);
                
                // Place player at start and display
                stepMap[playerRow][playerCol] = '*';
                  // Make sure visualizer is aware of map choice
                try {
                    if (mapChoice > 2) {
                        System.out.println("Using updateMap with health parameter for map " + mapChoice);
                        visualizer.updateMap(stepMap, 0, currentHealth);
                    } else {
                        System.out.println("Using updateMap without health parameter for map " + mapChoice);
                        visualizer.updateMap(stepMap, 0);
                    }
                } catch (Exception e) {
                    System.out.println("Error updating initial map: " + e.getMessage());
                    e.printStackTrace();
                }
                
                System.out.println("Starting position: (" + playerRow + "," + playerCol + ")");
                
                // Sleep a bit at the start to show initial position
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                // Track player position through solution
                int stepCounter = 0;
                
                // Debug the best solution path
                System.out.println("\nPath details:");
                for (Movement move : bestSolutionMoves) {
                    System.out.println("  - " + move);
                }
                
                // Skip the first move if it's the starting position
                int startIndex = 0;
                if (!bestSolutionMoves.isEmpty() && 
                    bestSolutionMoves.get(0).row == playerRow && 
                    bestSolutionMoves.get(0).col == playerCol) {
                    startIndex = 1;
                }
                
                for (int i = startIndex; i < bestSolutionMoves.size(); i++) {
                    Movement move = bestSolutionMoves.get(i);
                    stepCounter++;
                    
                    // Validate move
                    if (move.row < 0 || move.row >= stepMap.length || 
                        move.col < 0 || move.col >= stepMap[0].length) {
                        System.out.println("Error: Invalid move coordinates at step " + stepCounter);
                        continue;
                    }
                    
                    // Clear previous player position
                    if (originalMapState[playerRow][playerCol] == 'P') {
                        // If it's the player starting position, use the original symbol
                        stepMap[playerRow][playerCol] = 'P';
                    } else {
                        stepMap[playerRow][playerCol] = (originalMapState[playerRow][playerCol] == ' ') ? 
                                                         '-' : originalMapState[playerRow][playerCol];
                    }
                    
                    // Update player position to new position
                    playerRow = move.row;
                    playerCol = move.col;
                    
                    // Mark current position
                    if (originalMapState[playerRow][playerCol] != 'E') {
                        stepMap[playerRow][playerCol] = '*';
                    }
                    
                    // Update health based on action
                    if (move.action == 'L') {
                        currentHealth -= 50;
                    }
                    
                    // Print the step details first
                    System.out.println("Step " + stepCounter + ": " + move);
                      // Update display based on map choice
                    try {
                        if (mapChoice > 2) {
                            visualizer.updateMap(stepMap, stepCounter, currentHealth);
                        } else {
                            visualizer.updateMap(stepMap, stepCounter);
                        }
                    } catch (Exception e) {
                        System.out.println("Error updating map at step " + stepCounter + ": " + e.getMessage());
                        e.printStackTrace();
                        // Continue with next steps even if one fails
                    }
                    
                    // Fixed 150ms delay between steps - increased for better visibility
                    try {
                        Thread.sleep(150); // 150ms delay between steps
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            } catch (Exception e) {
                System.out.println("Error in visualization: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No path found.");
            System.out.println("Tries: " + tries);
        }
          // We're not closing Scanner here anymore
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
        
        // Early pruning - more aggressive conditions
        if (steps > minSteps && minSteps != Integer.MAX_VALUE) {
            return;
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
        if (originalCell != 'P' && originalCell != 'E' && originalCell != 'K') {
            map[r][c] = '*';
        }

        // Visualize only occasionally to improve performance
        if (tries % 10000 == 0) {
            visualizer.updateMap(map, tries);
        }

        if (map[r][c] == 'E' && hasKey) {
            if (steps < minSteps) {
                minSteps = steps;
                // Save the best map
                bestMap = new char[map.length][map[0].length];
                for (int i = 0; i < map.length; i++) {
                    bestMap[i] = map[i].clone();
                }
                  // Initialize if needed
                if (bestSolutionMoves == null) {
                    bestSolutionMoves = new ArrayList<>();
                }
                
                // Create a fresh list for the solution
                bestSolutionMoves = new ArrayList<>();
                
                // For Map 1 & 2, record the full path from start to end
                int[] startPos = findChar(map, 'P');
                if (startPos != null) {
                    // Start with position before first move
                    bestSolutionMoves.add(new Movement(startPos[0], startPos[1], -1, 'P'));
                }
                
                // Add all moves from current path
                for (Movement move : currentMoves) {
                    bestSolutionMoves.add(new Movement(move.row, move.col, move.direction, move.action));
                }
                
                // Add final move to exit if needed
                if (!currentMoves.isEmpty() && 
                    !(currentMoves.get(currentMoves.size()-1).row == r && 
                    currentMoves.get(currentMoves.size()-1).col == c)) {
                    bestSolutionMoves.add(new Movement(r, c, -1, 'E')); 
                }
                
                System.out.println("Found exit at (" + r + "," + c + ") in " + steps + " steps!");
                System.out.println("Saved solution with " + bestSolutionMoves.size() + " moves.");
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
            // Add key pickup to the current movements
            currentMoves.add(new Movement(r, c, -1, 'K')); // -1 direction means special action
        }        for (int d = 0; d < 4; d++) {
            int nr = r + dr[d], nc = c + dc[d];

            if (nr >= 0 && nr < map.length && nc >= 0 && nc < map[0].length &&
                    !visited[nr][nc] && !FileReader2DArray.isWall(map, nr, nc)) {
                // Don't allow entering 'E' before getting the key
                if (map[nr][nc] == 'E' && !hasKey)
                    continue;

                char temp = map[nr][nc];
                if (temp != 'E' && temp != 'P' && temp != 'K')
                    map[nr][nc] = '-'; // Mark path with -
                
                // Add this move to current path with proper direction and destination cell type
                currentMoves.add(new Movement(nr, nc, d, temp)); // Use temp to get original cell type
                
                backtrack(map, visited, nr, nc, steps + 1, hasKey);
                
                // Remove this move when backtracking
                if (!currentMoves.isEmpty()) {
                    currentMoves.remove(currentMoves.size() - 1);
                }

                if (temp != 'E' && temp != 'P' && temp != 'K')
                    map[nr][nc] = ' '; // Unmark if not correct path
            }
        }

        // Restore original cell content when backtracking
        if (originalCell != 'P' && originalCell != 'E' && originalCell != 'K') {
            map[r][c] = originalCell;
        }

        if (pickedKey) {
            hasKey = false; // Backtrack key pickup
            // Don't remove the movement record again, already handled above
        }
        visited[r][c] = false;
    }    static int bestHealth = -1;

    // Backtracking for map 3 with health and lava
    static void backtrackWithHealth(char[][] map, boolean[][] visited, int r, int c, int steps, boolean hasKey,
            int health) {
        // Early pruning
        if (steps >= minSteps && minSteps != Integer.MAX_VALUE) {
            return; // Skip if we already found a better solution
        }
        
        // Prevent infinite loops with try limit
        if (tries >= 10000000) {
            if (minSteps == Integer.MAX_VALUE) {
                System.out.println("No path found (try limit reached).");
            }
            return;
        }
        tries++;

        // Only update visualization occasionally to improve performance
        if (tries % 10000 == 0) {
            // First clear any * from the map (previous player position)
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[0].length; j++) {
                    if (map[i][j] == '*') {
                        map[i][j] = (map[i][j] == '*') ? '-' : map[i][j];
                    }
                }
            }
            
            // Mark current player position with *
            char origCell = map[r][c];
            if (origCell != 'P' && origCell != 'E' && origCell != 'K' && origCell != 'L') {
                map[r][c] = '*';
            }
            
            visualizer.updateMap(map, tries, health);
            
            // Restore cell to original state
            if (origCell != 'P' && origCell != 'E' && origCell != 'K' && origCell != 'L') {
                map[r][c] = origCell;
            }
        }

        // Stop if health is depleted
        if (health <= 0) {
            return;
        }

        // Exit condition - reached exit with key
        if (map[r][c] == 'E' && hasKey) {
            if (steps < minSteps || (steps == minSteps && health > bestHealth)) {
                minSteps = steps;
                bestHealth = health;
                bestMap = new char[map.length][map[0].length];
                for (int i = 0; i < map.length; i++) {
                    bestMap[i] = map[i].clone();
                }
                
                // Save the current path as the best solution
                bestSolutionMoves.clear();
                bestSolutionMoves = copyMovementList(currentMoves);
                
                // Add final move to exit
                bestSolutionMoves.add(new Movement(r, c, -1, 'E'));
                
                System.out.println("Found exit at (" + r + "," + c + ") in " + steps + " steps with " + health + " health!");
                System.out.println("Solution recorded with " + bestSolutionMoves.size() + " movements.");
            }
            
            // Record this solution
            char[][] solutionMap = new char[map.length][map[0].length];
            for (int i = 0; i < map.length; i++) {
                solutionMap[i] = map[i].clone();
            }
            allExitMaps.add(solutionMap);
            allExitSteps.add(steps);
            allExitHealths.add(health);
            
            // Show best solution so far
            visualizer.updateMap(bestMap, tries, health);
            return;
        }

        // Mark as visited
        visited[r][c] = true;
        
        // Process special cells
        boolean pickedKey = false;
        if (map[r][c] == 'K' && !hasKey) {
            hasKey = true;
            pickedKey = true;
            System.out.println("Key Taken at step " + steps);
            // Don't add a movement here - it's handled in the recursive call
        }
        
        // Try each direction
        for (int d = 0; d < 4; d++) {
            int nr = r + dr[d], nc = c + dc[d];
            
            // Validate the move
            if (nr < 0 || nr >= map.length || nc < 0 || nc >= map[0].length ||
                visited[nr][nc] || FileReader2DArray.isWall(map, nr, nc)) {
                continue;
            }
            
            // Don't allow entering exit without key
            if (map[nr][nc] == 'E' && !hasKey) {
                continue;
            }
            
            // Create a movement record for this step
            char cellType = map[nr][nc];
            Movement move = new Movement(nr, nc, d, cellType);
            currentMoves.add(move);
            
            // Calculate next health
            int nextHealth = health;
            if (cellType == 'L') {
                nextHealth -= 50; // Lava damage
            }
            
            // Skip if this move would kill us
            if (nextHealth <= 0) {
                currentMoves.remove(currentMoves.size() - 1); // Remove the unsuccessful move
                continue;
            }
            
            // Temporary mark our path
            char tempChar = map[nr][nc];
            if (tempChar != 'E' && tempChar != 'P' && tempChar != 'K' && tempChar != 'L') {
                map[nr][nc] = '-';
            }
            
            // Recursive call
            backtrackWithHealth(map, visited, nr, nc, steps + 1, hasKey, nextHealth);
            
            // Restore the map
            if (tempChar != 'E' && tempChar != 'P' && tempChar != 'K' && tempChar != 'L') {
                map[nr][nc] = tempChar;
            }
            
            // Remove this move when backtracking
            currentMoves.remove(currentMoves.size() - 1);
        }
        
        // Restore key status when backtracking
        if (pickedKey) {
            hasKey = false;
        }
        
        // Mark as not visited
        visited[r][c] = false;
    }    static void backtrackMap4(
            char[][] map,
            boolean[][][][][] visited, // [row][col][pickaxe][sword][gold]
            int r, int c, int steps, boolean hasKey,
            int health, boolean hasPickaxe, boolean hasSword, int gold) {
        
        // Only update visualization occasionally to improve performance
        if (tries % 10000 == 0) {
            // First clear any * from the map (previous player position)
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[0].length; j++) {
                    if (map[i][j] == '*') {
                        map[i][j] = '-';
                    }
                }
            }

            // Mark current player position with *
            char origCell = map[r][c];
            if (origCell != 'P' && origCell != 'E' && origCell != 'K' && origCell != 'L' &&
                    origCell != 'X' && origCell != 'G' && origCell != 'N' && origCell != 'W' &&
                    origCell != 'M') {
                map[r][c] = '*';
            }

            visualizer.updateMap(map, tries, health);
            
            // Restore cell to original state if it was marked
            if (origCell != 'P' && origCell != 'E' && origCell != 'K' && origCell != 'L' &&
                    origCell != 'X' && origCell != 'G' && origCell != 'N' && origCell != 'W' &&
                    origCell != 'M' && map[r][c] == '*') {
                map[r][c] = origCell;
            }
        }
        tries++;

        // Stop if health is depleted
        if (health <= 0) {
            return;
        }

        // Check if we've reached the exit with a key
        if (map[r][c] == 'E' && hasKey) {
            if (steps < minSteps || (steps == minSteps && health > bestHealth)) {
                minSteps = steps;
                bestHealth = health;
                bestMap = new char[map.length][map[0].length];
                for (int i = 0; i < map.length; i++) {
                    bestMap[i] = map[i].clone();
                }
                
                System.out.println("Found exit at (" + r + "," + c + ") in " + steps + " steps with " + health + " health!");
            }
            
            // Record this solution
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

        // State encoding for visited check
        int pickaxeInt = hasPickaxe ? 1 : 0;
        int swordInt = hasSword ? 1 : 0;
        int goldInt = Math.min(gold, 200); // Limit gold value for visited array size
        
        // Avoid revisiting same state
        if (visited[r][c][pickaxeInt][swordInt][goldInt]) {
            return;
        }
        visited[r][c][pickaxeInt][swordInt][goldInt] = true;

        // Process pickups and special cells
        // Pickaxe
        if (map[r][c] == 'X' && !hasPickaxe) {
            hasPickaxe = true;
            map[r][c] = ' ';
            System.out.println("Pickaxe obtained at (" + r + "," + c + ")");
        }
        // Sword
        if (map[r][c] == 'W' && !hasSword) {
            hasSword = true;
            map[r][c] = ' ';
            System.out.println("Sword obtained at (" + r + "," + c + ")");
        }
        // Gold vein
        if (map[r][c] == 'G') {
            if (hasPickaxe) {
                gold += 10;
                map[r][c] = ' '; // Remove gold vein
                System.out.println("Gold mined at (" + r + "," + c + "), total gold: " + gold);
            }
        }
        // Monster
        if (map[r][c] == 'M') {
            if (hasSword) {
                gold += 10;
                map[r][c] = ' '; // Remove monster
                System.out.println("Monster defeated at (" + r + "," + c + "), total gold: " + gold);
            } else {
                health -= 100;
                System.out.println("Attacked by monster at (" + r + "," + c + "), health now: " + health);
                if (health <= 0) {
                    return;
                }
            }
        }
        // NPC for buying key
        if (map[r][c] == 'N') {
            if (gold >= 50 && !hasKey) {
                hasKey = true;
                gold -= 50;
                System.out.println("Key bought from NPC at (" + r + "," + c + "), gold left: " + gold);
            }
        }
        // Lava damage
        if (map[r][c] == 'L') {
            health -= 50;
            System.out.println("Stepped on lava at (" + r + "," + c + "), health now: " + health);
            if (health <= 0) {
                return;
            }
        }
        
        // Try each direction
        for (int d = 0; d < 4; d++) {
            int nr = r + dr[d], nc = c + dc[d];
            
            // Check if move is valid
            if (nr < 0 || nr >= map.length || nc < 0 || nc >= map[0].length ||
                FileReader2DArray.isWall(map, nr, nc)) {
                continue;
            }
            
            // Don't allow entering exit without key
            if (map[nr][nc] == 'E' && !hasKey) {
                continue;
            }
            
            backtrackMap4(map, visited, nr, nc, steps + 1, hasKey, health, hasPickaxe, hasSword, gold);
        }        // Cleanup when backtracking - restore state
        visited[r][c][pickaxeInt][swordInt][goldInt] = false;
    }    // Main backtracking for map 5
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
            char[][] pathMap) {
        // Early pruning
        if (steps > minSteps && minSteps != Integer.MAX_VALUE) {
            return; // Skip if we already found a better solution
        }
        
        // Only update visualization occasionally to improve performance
        if (tries % 10000 == 0) {
            // First clear any * from pathMap (previous player position)
            for (int i = 0; i < pathMap.length; i++) {
                for (int j = 0; j < pathMap[0].length; j++) {
                    if (pathMap[i][j] == '*') {
                        pathMap[i][j] = '-';
                    }
                }
            }

            // Mark current player position with *
            char origCell = pathMap[r][c];
            boolean canBeMarked = (origCell == ' ' || origCell == '-');
            if (canBeMarked) {
                pathMap[r][c] = '*';
            }
            
            visualizer.updateMap(pathMap, tries, health);
            
            // Restore cell if needed
            if (canBeMarked) {
                pathMap[r][c] = origCell;
            }
        }
        tries++;

        // Encode state to prevent revisiting the same state
        String state = r + "," + c + "," + hasKey + "," + hasPickaxe + "," + hasSword + "," + gold + "," + health
                + "|" + encodeGoldVeins(map)
                + "|" + encodeMonsters(map)
                + "|" + encodeLogs(map);
        if (visited.contains(state)) {
            return; // Skip if we've seen this state before
        }
        visited.add(state);

        // Stop if health is depleted
        if (health <= 0) {
            return;
        }

        // Check if we've reached the exit with a key
        if (map[r][c] == 'E' && hasKey) {
            if (steps < minSteps || (steps == minSteps && health > bestHealth)) {
                minSteps = steps;
                
                // Copy the pathMap as the bestMap
                bestMap = new char[pathMap.length][pathMap[0].length];
                for (int i = 0; i < pathMap.length; i++)
                    bestMap[i] = pathMap[i].clone();
                    
                bestHealth = health;
                
                // Save the current path as the best solution using deep copy
                bestSolutionMoves.clear();
                bestSolutionMoves = copyMovementList(currentMoves);
                
                // Add final move to exit if not already recorded
                bestSolutionMoves.add(new Movement(r, c, -1, 'E'));
                
                System.out.println("Found exit at (" + r + "," + c + ") in " + steps + " steps with " + health + " health!");
                System.out.println("Solution recorded with " + bestSolutionMoves.size() + " movements.");
            }
            
            // Record this solution
            char[][] solutionMap = new char[pathMap.length][pathMap[0].length];
            for (int i = 0; i < pathMap.length; i++) {
                solutionMap[i] = pathMap[i].clone();
            }
            allExitMaps.add(solutionMap);
            allExitSteps.add(steps);
            allExitHealths.add(health);

            visualizer.updateMap(bestMap, tries, health);
            return;
        }

        // Process special cells and pickups
        boolean pickedPickaxe = false, pickedSword = false, minedGold = false, boughtKey = false, defeatedMonster = false;

        // Pickaxe
        if (map[r][c] == 'X' && !hasPickaxe) {
            hasPickaxe = true;
            pickedPickaxe = true;
            map[r][c] = ' ';
            System.out.println("Pickaxe obtained at (" + r + "," + c + ")");
            // Don't add movement here - it will be added during movement
        }
        // Sword
        if (map[r][c] == 'W' && !hasSword) {
            hasSword = true;
            pickedSword = true;
            map[r][c] = ' ';
            System.out.println("Sword obtained at (" + r + "," + c + ")");
            // Don't add movement here - it will be added during movement
        }
        // Gold vein
        if (map[r][c] == 'G') {
            if (hasPickaxe) {
                gold += 10;
                map[r][c] = ' '; // Remove gold vein
                minedGold = true;
                System.out.println("Gold mined at (" + r + "," + c + "), total gold: " + gold);
                // Don't add movement here - it will be added during movement
            }
        }
        // Monster
        if (map[r][c] == 'M') {
            if (hasSword) {
                gold += 10;
                map[r][c] = ' '; // Remove monster
                defeatedMonster = true;
                System.out.println("Monster defeated at (" + r + "," + c + "), total gold: " + gold);
                // Don't add movement here - it will be added during movement
            } else {
                health -= 100;
                System.out.println("Attacked by monster at (" + r + "," + c + "), health now: " + health);
                if (health <= 0) {
                    return;
                }
            }
        }
        // NPC for buying key
        if (map[r][c] == 'N') {
            if (gold >= 50 && !hasKey) {
                hasKey = true;
                gold -= 50;
                boughtKey = true;
                System.out.println("Key bought from NPC at (" + r + "," + c + "), gold left: " + gold);
                // Don't add movement here - it will be added during movement
            }
        }
        // Lava damage
        if (map[r][c] == 'L') {
            health -= 50;
            System.out.println("Stepped on lava at (" + r + "," + c + "), health now: " + health);
            // Don't add movement here - it will be added during movement
            if (health <= 0) {
                return;
            }
        }

        // Try each direction
        for (int d = 0; d < 4; d++) {
            int nr = r + dr[d], nc = c + dc[d];
<<<<<<< Updated upstream
            if (nr < 0 || nr >= map.length || nc < 0 || nc >= map[0].length)
                continue;

            // Simulate log movement
            char[][] newMap = copyMap(map);
            moveLogs(newMap);

            char afterMove = newMap[nr][nc];

            // Check if can step
            boolean canStep = false;
            if (afterMove == ' ' || afterMove == 'K' || afterMove == 'E' || afterMove == 'L' || afterMove == 'X'
                    || afterMove == 'G' || afterMove == 'N' || afterMove == 'W' || afterMove == 'M') {
                canStep = true;
            } else if (afterMove == 'O') {
                canStep = true; // log on water
            } else if (afterMove == 'A') {
                canStep = false; // water without log
            } else {
                canStep = false; // wall or S
=======
            
            // Check if move is valid
            if (nr < 0 || nr >= map.length || nc < 0 || nc >= map[0].length ||
                FileReader2DArray.isWall(map, nr, nc)) {
                continue;
>>>>>>> Stashed changes
            }
            
            // Don't allow entering exit without key
            if (map[nr][nc] == 'E' && !hasKey) {
                continue;
            }
            
            // Record movement before recursing
            char actionChar;
            if (map[nr][nc] == 'X') actionChar = 'X';      // Pickaxe
            else if (map[nr][nc] == 'W') actionChar = 'W'; // Sword
            else if (map[nr][nc] == 'G' && hasPickaxe) actionChar = 'G'; // Mine gold
            else if (map[nr][nc] == 'M') actionChar = hasSword ? 'M' : map[nr][nc]; // Kill monster
            else if (map[nr][nc] == 'N' && gold >= 50 && !hasKey) actionChar = 'N'; // Buy key
            else if (map[nr][nc] == 'K') actionChar = 'K'; // Get key
            else if (map[nr][nc] == 'L') actionChar = 'L'; // Lava
            else if (map[nr][nc] == 'O') actionChar = 'O'; // Log
            else if (map[nr][nc] == 'E') actionChar = 'E'; // Exit
            else actionChar = map[nr][nc];
            
            // Add movement to current path
            currentMoves.add(new Movement(nr, nc, d, actionChar));
            
            // Recursive call
            backtrackMap5(map, visited, nr, nc, steps + 1, hasKey, health, hasPickaxe, hasSword, gold, pathMap);
            
            // Remove move when backtracking
            if (!currentMoves.isEmpty()) {
                currentMoves.remove(currentMoves.size() - 1);
            }
        }

        // Restore state for backtracking
        if (pickedPickaxe) {
            hasPickaxe = false;
            map[r][c] = 'X';
        }
        if (pickedSword) {
            hasSword = false;
            map[r][c] = 'W';
        }
        if (minedGold) {
            gold -= 10;
            map[r][c] = 'G';
        }
        if (defeatedMonster) {
            gold -= 10;
            map[r][c] = 'M';
        }
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
                if (map[i][j] == 'M')
                    sb.append(i).append(',').append(j).append(';');
            }
        }
        return sb.toString();
    }

    // Helper to encode gold vein positions for visited state
    static String encodeGoldVeins(char[][] map) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == 'G')
                    sb.append(i).append(',').append(j).append(';');
            }
        }
        return sb.toString();
    }

    // Helper to encode log positions for visited state
    static String encodeLogs(char[][] map) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == 'O')
                    sb.append(i).append(',').append(j).append(';');
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
<<<<<<< Updated upstream

=======
        
    // Handle vertical log movement for any vertical water channels
        // Scan each column for vertical water channels
        for (int col = 0; col < map[0].length; col++) {
            int top = -1, bottom = -1;
            
            // Find continuous vertical water channels
            for (int r = 0; r < map.length; r++) {
                if (map[r][col] == 'A' || map[r][col] == 'O') {
                    if (top == -1) {
                        top = r;
                    }
                    bottom = r;
                } else if (top != -1) {
                    // Found a break in the vertical water channel, process this segment
                    if (bottom > top) {
                        processVerticalLogMovement(map, top, bottom, col);
                    }
                    // Reset to find the next vertical water channel in this column
                    top = -1;
                    bottom = -1;
                }
            }
            
            // Process the last vertical water channel in this column if it exists
            if (top != -1 && bottom > top) {
                processVerticalLogMovement(map, top, bottom, col);
            }
        }
        
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
    }

    static int[] portal1 = null;
    static int[] portal2 = null;

    static void backtrackMap6(
=======
    }    static void backtrackMap6(
>>>>>>> Stashed changes
            char[][] map,
            HashSet<String> visited,
            int r, int c,
            int steps,
            boolean hasKey,
            int health,
            boolean hasPickaxe,
            boolean hasSword,
            int gold,
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

        // Limit visualization to reduce processing
        if (tries % 10000 == 0) {
            visualizer.updateMap(pathMap, tries, health);
        }
        tries++;

        // Handle teleportation
        if (map[r][c] == '1' && portal2 != null) {
            r = portal2[0];
            c = portal2[1];
        } else if (map[r][c] == '2' && portal1 != null) {
            r = portal1[0];
            c = portal1[1];
        }
        
        // Encode state to prevent revisiting the same state
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

        // Check if we've reached the exit with a key
        if (map[r][c] == 'E' && hasKey) {            // If this solution is better than the current best
            if (steps < minSteps || (steps == minSteps && health > bestHealth)) {
                minSteps = steps;
                // Copy the pathMap as the bestMap
                bestMap = new char[pathMap.length][pathMap[0].length];
                for (int i = 0; i < pathMap.length; i++)
                    bestMap[i] = pathMap[i].clone();
                bestHealth = health;
                System.out.println("Found exit at (" + r + "," + c + ") in " + steps + " steps with health: " + health);
            }
            
            // Record this solution
            char[][] solutionMap = new char[map.length][map[0].length];
            for (int i = 0; i < map.length; i++) {
                solutionMap[i] = map[i].clone();
            }
            allExitMaps.add(solutionMap);
            allExitSteps.add(steps);
            allExitHealths.add(health);
            
            // Restore original cell when backtracking
            if (canBeMarked) {
                pathMap[r][c] = originalCell;
            }
            return;
        }
        
        // Process pickups and special cells
        // Pickaxe
        if (map[r][c] == 'X' && !hasPickaxe) {
            hasPickaxe = true;
            map[r][c] = ' ';
            System.out.println("Pickaxe obtained at (" + r + "," + c + ")");
        }
        // Sword
        if (map[r][c] == 'W' && !hasSword) {
            hasSword = true;
            map[r][c] = ' ';
            System.out.println("Sword obtained at (" + r + "," + c + ")");
        }
        // Gold vein
        if (map[r][c] == 'G') {
            if (hasPickaxe) {
                gold += 10;
                map[r][c] = ' '; // Remove gold vein
                System.out.println("Gold mined at (" + r + "," + c + "), total gold: " + gold);
            }
        }
        // Monster
        if (map[r][c] == 'M') {
            if (hasSword) {
                gold += 10;
                map[r][c] = ' '; // Remove monster
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
        // NPC for buying key
        if (map[r][c] == 'N') {
            if (gold >= 50 && !hasKey) {
                hasKey = true;
                gold -= 50;
                System.out.println("Key bought from NPC at (" + r + "," + c + "), gold left: " + gold);
            }
        }
        // Lava damage
        if (map[r][c] == 'L') {
            health -= 50;
            System.out.println("Stepped on lava at (" + r + "," + c + "), health now: " + health);
            if (health <= 0) {
                // Restore original cell when backtracking
                if (canBeMarked) {
                    pathMap[r][c] = originalCell;
                }
                return;
            }
        }
        
        // Apply map changes for log movement
        moveLogs(map);
        
        // Try each direction
        int currentRow = r;
        int currentCol = c;
        
        for (int d = 0; d < 4; d++) {
            int nr = currentRow + dr[d], nc = currentCol + dc[d];
            
            // Skip invalid moves
            if (nr < 0 || nr >= map.length || nc < 0 || nc >= map[0].length || 
                FileReader2DArray.isWall(map, nr, nc)) {
                continue;
            }
            
            // Teleport logic
            int destRow = nr;
            int destCol = nc;
            
            if (map[nr][nc] == '1' && portal2 != null) {
                destRow = portal2[0];
                destCol = portal2[1];
                backtrackMap6(map, visited, destRow, destCol, steps + 1, hasKey, health, hasPickaxe, hasSword, gold, pathMap);
            } else if (map[nr][nc] == '2' && portal1 != null) {
                destRow = portal1[0];
                destCol = portal1[1];
                backtrackMap6(map, visited, destRow, destCol, steps + 1, hasKey, health, hasPickaxe, hasSword, gold, pathMap);
            } else {
                // Don't allow entering exit without key
                if (map[nr][nc] == 'E' && !hasKey) {
                    continue;
                }
                
                // Create a copy of map for this branch
                char[][] newMap = new char[map.length][map[0].length];
                for (int i = 0; i < map.length; i++) {
                    newMap[i] = map[i].clone();
                }
                
                backtrackMap6(newMap, visited, nr, nc, steps + 1, hasKey, health, hasPickaxe, hasSword, gold, pathMap);
            }
        }
        
        // Restore original cell when backtracking
        if (canBeMarked) {
            pathMap[r][c] = originalCell;
        }
        
        // Remove state when backtracking
        visited.remove(state);
                }
                
                System.out.println("Saved solution moves: " + bestSolutionMoves.size());
            }
            
            // Record all exit solutions for visualization
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
        }        boolean pickedPickaxe = false, pickedSword = false, minedGold = false, boughtKey = false,
                defeatedMonster = false;

        // Process items and actions at current position
        // Pickaxe
        if (map[r][c] == 'X' && !hasPickaxe) {
            hasPickaxe = true;
            pickedPickaxe = true;
            map[r][c] = ' ';
            System.out.println("Pickaxe obtained at (" + r + "," + c + ")");
            // No need to add movement here - it will be tracked in the direction we moved
        }
        // Sword
        if (map[r][c] == 'W' && !hasSword) {
            hasSword = true;
            pickedSword = true;
            map[r][c] = ' ';
            System.out.println("Sword obtained at (" + r + "," + c + ")");
            // No need to add movement here - it will be tracked in the direction we moved
        }
        // Gold vein
        if (map[r][c] == 'G') {
            if (hasPickaxe) {
                gold += 10;
                map[r][c] = ' '; // Remove gold vein
                minedGold = true;
                System.out.println("Gold mined at (" + r + "," + c + "), total gold: " + gold);
                // No need to add movement here - it will be tracked in the direction we moved
            }
        }
        // Monster
        if (map[r][c] == 'M') {
            if (hasSword) {
                gold += 10;
                map[r][c] = ' '; // Remove monster
                defeatedMonster = true;
                System.out.println("Monster defeated at (" + r + "," + c + "), total gold: " + gold);
                // No need to add movement here - it will be tracked in the direction we moved
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
        // NPC for buying key (50 gold)
        if (map[r][c] == 'N') {
            if (gold >= 50 && !hasKey) {
                hasKey = true;
                gold -= 50;
                boughtKey = true;
                System.out.println("Key bought from NPC at (" + r + "," + c + "), gold left: " + gold);
                // Track this important action
                currentMoves.add(new Movement(r, c, -1, 'N'));
            }
        }
        // Lava damage
        if (map[r][c] == 'L') {
            health -= 50;
            System.out.println("Stepped on lava at (" + r + "," + c + "), health now: " + health);
            if (health <= 0) {
                // Restore original cell when backtracking
                if (canBeMarked) {
                    pathMap[r][c] = originalCell;
                }
                return;
            }
        }        // Portal logic: teleport if on '1' or '2'
        if (map[r][c] == '1' && portal2 != null) {
            int destRow = portal2[0] - 1;
            int destCol = portal2[1];
            // Only teleport if destination is within bounds and not a wall
            if (destRow >= 0 && !FileReader2DArray.isWall(map, destRow, destCol)) {
                System.out.println("Portal used: from (1) at (" + r + "," + c + ") to above (2) at (" + destRow + ","
                        + destCol + ")");
                
                // Track teleport movement
                currentMoves.add(new Movement(destRow, destCol, -1, '1')); // Direction -1 indicates teleport
                
                backtrackMap6(map, visited, destRow, destCol, steps + 1, hasKey, health, hasPickaxe, hasSword, gold,
                        pathMap);
                        
                // Remove the move when backtracking
                if (!currentMoves.isEmpty()) {
                    currentMoves.remove(currentMoves.size() - 1);
                }
                
                return; // Do not continue normal movement from here
            }
        }

        if (map[r][c] == '2' && portal1 != null) {
            int destRow = portal1[0] - 1;
            int destCol = portal1[1];
            if (destRow >= 0 && !FileReader2DArray.isWall(map, destRow, destCol)) {
                System.out.println("Portal used: from (2) at (" + r + "," + c + ") to above (1) at (" + destRow + ","
                        + destCol + ")");
                
                // Track teleport movement
                currentMoves.add(new Movement(destRow, destCol, -1, '2')); // Direction -1 indicates teleport
                
                backtrackMap6(map, visited, destRow, destCol, steps + 1, hasKey, health, hasPickaxe, hasSword, gold,
                        pathMap);
                
                // Remove the move when backtracking
                if (!currentMoves.isEmpty()) {
                    currentMoves.remove(currentMoves.size() - 1);
                }
                
                return;
            }
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

            // Record this move with the proper direction and action
            currentMoves.add(new Movement(nr, nc, d, afterMove));
            
            backtrackMap6(newMap, visited, nr, nc, steps + 1, hasKey, health, hasPickaxe, hasSword, gold, pathMap);
            
            // Remove the move when backtracking
            if (!currentMoves.isEmpty()) {
                currentMoves.remove(currentMoves.size() - 1);
            }
        }        // Restore original cell when backtracking
        if (canBeMarked) {
            pathMap[r][c] = originalCell;
        }

        // Restore states when backtracking
        if (pickedPickaxe) {
            hasPickaxe = false;
            map[r][c] = 'X';
        }
        if (pickedSword) {
            hasSword = false;
            map[r][c] = 'W';
        }
        if (minedGold) {
            gold -= 10;
            map[r][c] = 'G';
        }
        if (defeatedMonster) {
            gold -= 10;
            map[r][c] = 'M';
        }
        if (boughtKey) {
            hasKey = false;
            gold += 50; // Restore the full gold amount
        }
    }
}
