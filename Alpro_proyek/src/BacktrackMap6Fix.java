import java.util.*;

public class BacktrackMap6Fix {
    // Method signature
    static void backtrackMap6(
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
        
        // Update visualization occasionally
        if (tries % 10000 == 0) {
            visualizer.updateMap(map, tries, health);
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

        // Encode state to prevent revisiting
        String state = r + "," + c + "," + hasKey + "," + hasPickaxe + "," + hasSword + "," + gold + "," + health;
        if (visited.contains(state)) {
            return;
        }
        visited.add(state);

        // Check game over conditions
        if (health <= 0) {
            return;
        }

        // Check if exit reached with key
        if (map[r][c] == 'E' && hasKey) {
            if (steps < minSteps || (steps == minSteps && health > bestHealth)) {
                minSteps = steps;
                bestHealth = health;
                
                // Save solution map
                bestMap = new char[map.length][map[0].length];
                for (int i = 0; i < map.length; i++) {
                    bestMap[i] = map[i].clone();
                }
                
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
            return;
        }

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
        if (map[r][c] == 'G' && hasPickaxe) {
            gold += 10;
            map[r][c] = ' ';
        }
        
        // Monster
        if (map[r][c] == 'M') {
            if (hasSword) {
                gold += 10;
                map[r][c] = ' ';
            } else {
                health -= 100;
                if (health <= 0) return;
            }
        }
        
        // NPC for buying key
        if (map[r][c] == 'N' && gold >= 50 && !hasKey) {
            hasKey = true;
            gold -= 50;
        }
        
        // Lava damage
        if (map[r][c] == 'L') {
            health -= 50;
            if (health <= 0) return;
        }
        
        // Try each direction
        for (int d = 0; d < 4; d++) {
            int nr = r + dr[d], nc = c + dc[d];
            
            // Skip invalid moves
            if (nr < 0 || nr >= map.length || nc < 0 || nc >= map[0].length || 
                FileReader2DArray.isWall(map, nr, nc)) {
                continue;
            }
            
            // Don't allow entering exit without key
            if (map[nr][nc] == 'E' && !hasKey) {
                continue;
            }
            
            // Copy map for this branch
            char[][] mapCopy = new char[map.length][map[0].length];
            for (int i = 0; i < map.length; i++) {
                mapCopy[i] = map[i].clone();
            }
            
            backtrackMap6(mapCopy, visited, nr, nc, steps + 1, hasKey, health, hasPickaxe, hasSword, gold, pathMap);
        }
        
        // Remove state when backtracking
        visited.remove(state);
    }
}
