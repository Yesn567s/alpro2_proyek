import java.util.Scanner;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;

public class App {
    // All the existing static variables remain unchanged
    
    public static void main(String[] args) throws Exception {
        // Declare variables outside try block so they're accessible throughout the method
        int mapChoice = 0;
        String mapFile = null;
        int initialHealth = 0;
        
        // Get map choice from user, but avoid closing System.in
        try (Scanner scInt = new Scanner(System.in)) {
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
                    break;
                default:
                    System.out.println("Invalid map selection.");
                    return;
            }
        } // Scanner is automatically closed here
        
        // Continue with the rest of the method using the variables we've set
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
            System.out.println("Path found:");
            FileReader2DArray.print2DCharMap(bestMap);
            System.out.println("Tries: " + tries);
            System.out.println("Steps: " + minSteps);
            if (mapChoice == 3 || mapChoice == 4 || mapChoice == 5 || mapChoice == 6 || mapChoice == 7) {
                visualizer.updateMap(bestMap, tries, bestHealth);
                System.out.println("Remaining Health: " + bestHealth);
            }
              // Display step-by-step solution summary
            System.out.println("\nStep-by-Step Solution:");
            System.out.println("Total steps: " + bestSolutionMoves.size());
              
            // Announce step-by-step visualization without requiring user input
            System.out.println("\nShowing step-by-step visualization in 2 seconds...");
            try {
                // Just wait a bit before proceeding with visualization
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Reset the map to initial state
            char[][] initialMap = FileReader2DArray.read2DCharMapFromFile(mapFile);
            int currentHealth = initialHealth;
            
            // Use the appropriate updateMap method based on the map choice
            if (mapChoice > 2) {
                visualizer.updateMap(initialMap, 0, currentHealth);  // Maps with health
            } else {
                visualizer.updateMap(initialMap, 0);  // Maps without health
            }
              
            char[][] stepMap = copyMap(initialMap);
              
            // Validate solution before visualization
            if (bestSolutionMoves == null || bestSolutionMoves.isEmpty()) {
                System.out.println("Error: No solution moves recorded. Cannot visualize steps.");
                return;
            }
            
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
            if (mapChoice > 2) {
                visualizer.updateMap(stepMap, 0, currentHealth);
            } else {
                visualizer.updateMap(stepMap, 0);
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
                
                // Mark current position with a star
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
                if (mapChoice > 2) {
                    visualizer.updateMap(stepMap, stepCounter, currentHealth);
                } else {
                    visualizer.updateMap(stepMap, stepCounter);
                }
                
                // Fixed 150ms delay between steps - increased for better visibility
                try {
                    Thread.sleep(150); // 150ms delay between steps
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                }
        } else {
            System.out.println("No path found.");
            System.out.println("Tries: " + tries);
        }
    }

    // The remaining methods stay the same
}
