import java.util.*;

public class BacktrackMap4Fix {
    
    static void backtrackMap4(
            char[][] map,
            boolean[][][][][] visited, // [row][col][pickaxe][sword][gold]
            int r, int c, int steps, boolean hasKey,
            int health, boolean hasPickaxe, boolean hasSword, int gold) {
        
        // Only update visualization occasionally
        if (tries % 10000 == 0) {
            // Mark current player position with *
            char origCell = map[r][c];
            if (origCell != 'P' && origCell != 'E' && origCell != 'K' && origCell != 'L' &&
                    origCell != 'X' && origCell != 'G' && origCell != 'N' && origCell != 'W' &&
                    origCell != 'M') {
                map[r][c] = '*';
            }

            visualizer.updateMap(map, tries, health);
            
            // Restore cell to original state if it was marked
            if (map[r][c] == '*') {
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
        }
        // Sword
        if (map[r][c] == 'W' && !hasSword) {
            hasSword = true;
            map[r][c] = ' ';
        }
        // Gold vein
        if (map[r][c] == 'G') {
            if (hasPickaxe) {
                gold += 10;
                map[r][c] = ' '; // Remove gold vein
            }
        }
        // Monster
        if (map[r][c] == 'M') {
            if (hasSword) {
                gold += 10;
                map[r][c] = ' '; // Remove monster
            } else {
                health -= 100;
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
            }
        }
        // Lava damage
        if (map[r][c] == 'L') {
            health -= 50;
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
        }

        // Cleanup when backtracking - restore state
        visited[r][c][pickaxeInt][swordInt][goldInt] = false;
    }
}
