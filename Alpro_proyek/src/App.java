import java.util.Scanner;
import java.util.Random;

public class App {
    static int[] dr = { -1, 1, 0, 0 }; // up, down, left, right
    static int[] dc = { 0, 0, -1, 1 };
    static int tries = 0;
    static int minSteps = Integer.MAX_VALUE;
    static char[][] bestMap = null;
    static boolean found = false;

    public static void main(String[] args) throws Exception {
        Scanner scInt = new Scanner(System.in);
        System.out.print("Select map to use: ");
        int mapChoice = scInt.nextInt();
        String mapFile;
        int initialHealth = 0;
        switch (mapChoice) {
            case 1:
                mapFile = "Alpro_proyek/src/Z_array1.txt";
                mapFile = "Alpro_proyek/src/Z_array1.txt";
                break;
            case 2:
                mapFile = "Alpro_proyek/src/Z_array2.txt";
                break;
            case 3:
                mapFile = "Alpro_proyek/src/Z_array3.txt";
                initialHealth = 200;
                break;
            case 4:
                mapFile = "Alpro_proyek/src/Z_array4.txt";
                initialHealth = 200;
                break;
            case 5:
                mapFile = "Alpro_proyek/src/Z_array5.txt";
                initialHealth = 200;
                mapFile = "Alpro_proyek/src/Z_array2.txt";
                break;
            default:
                System.out.println("Invalid map selection.");
                scInt.close();
                return;
        }
        scInt.close();

        char[][] map = FileReader2DArray.read2DCharMapFromFile(mapFile);
        int[] start = findChar(map, 'P');
        if (start == null) {
            System.out.println("Player not found!");
            return;
        }

        if (mapChoice == 3) {
            boolean[][] visited = new boolean[map.length][map[0].length];
            backtrackWithHealth(map, visited, start[0], start[1], 0, false, initialHealth);
        } else if (mapChoice == 4) {
            hasSword = false;
            hasPickaxe = false;
            gold = 0;
            bestHealth = initialHealth;
            // boolean[][][][] visited = new boolean[map.length][map[0].length][2][11]; //
            // gold up to 10
            // backtrackMap4(map, visited, start[0], start[1], 0, false, initialHealth,
            // false, 0);

            // boolean[][][][][] visited = new boolean[map.length][map[0].length][2][2][31];
            // // [row][col][sword][pickaxe][gold
            // // up to 30]
            // backtrackMap4(map, visited, start[0], start[1], 0, false, initialHealth,
            // false, false, 0);

            boolean[][][][][][] visited = new boolean[map.length][map[0].length][2][2][101][2]; // last [2] for
                                                                                                // monsterDir (0:left,
                                                                                                // 1:right)
            backtrackMap4(map, visited, start[0], start[1], 0, false, initialHealth, false, false, 0, MONSTER_RIGHT);
        } else {
            boolean[][] visited = new boolean[map.length][map[0].length];
            backtrack(map, visited, start[0], start[1], 0, false);
        }

        if (minSteps < Integer.MAX_VALUE) {
            System.out.println("Path found:");
            FileReader2DArray.print2DCharMap(bestMap);
            System.out.println("Tries: " + tries);
            System.out.println("Steps: " + minSteps);
            if (mapChoice == 3) {
                System.out.println("Remaining Health: " + bestHealth);
            }
            if (mapChoice == 3) {
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
        if (health <= 0)
            return;
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

    // Add this constant for direction
    static final int MONSTER_RIGHT = 1;
    static final int MONSTER_LEFT = -1;

    // Update your backtrackMap4 signature to include monsterDir
    static void backtrackMap4(char[][] map, boolean[][][][][][] visited, int r, int c, int steps, boolean hasKey,
            int health, boolean hasSword, boolean hasPickaxe, int gold, int monsterDir) {
        tries++;
        if (health <= 0)
            return;
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
        int swordIdx = hasSword ? 1 : 0;
        int pickaxeIdx = hasPickaxe ? 1 : 0;
        int monsterDirIdx = (monsterDir == MONSTER_RIGHT) ? 1 : 0;
        if (visited[r][c][swordIdx][pickaxeIdx][gold][monsterDirIdx])
            return;
        visited[r][c][swordIdx][pickaxeIdx][gold][monsterDirIdx] = true;

        boolean pickedSword = false, pickedPickaxe = false, minedGold = false, defeatedMonster = false, gotKey = false;
        char original = map[r][c];

        // Sword
        if (map[r][c] == 'W' && !hasSword) {
            hasSword = true;
            pickedSword = true;
            System.out.println("Sword obtained");
        }
        // Pickaxe
        if (map[r][c] == 'X' && !hasPickaxe) {
            hasPickaxe = true;
            pickedPickaxe = true;
            System.out.println("Pickaxe obtained");
        }
        // Gold vein
        if (map[r][c] == 'G' && hasPickaxe) {
            gold += 10;
            map[r][c] = ' '; // Remove gold vein
            minedGold = true;
            System.out.println("Gold mined, total gold: " + gold);
        }
        // Monster interaction (if player steps on monster)
        if (map[r][c] == 'M') {
            if (hasSword) {
                gold += 10;
                map[r][c] = ' '; // Remove monster
                defeatedMonster = true;
                System.out.println("Monster defeated, total gold: " + gold);
            } else {
                health -= 100;
                System.out.println("Attacked by monster! Health: " + health);
                if (health <= 0) {
                    // Undo actions for backtracking
                    if (pickedSword)
                        hasSword = false;
                    if (pickedPickaxe)
                        hasPickaxe = false;
                    if (minedGold)
                        map[r][c] = 'G';
                    if (defeatedMonster)
                        map[r][c] = 'M';
                    if (gotKey)
                        hasKey = false;
                    return;
                }
            }
        }
        // NPC
        if (map[r][c] == 'N') {
            System.out.println("At NPC: gold=" + gold + " hasKey=" + hasKey);
            if (gold >= 20 && !hasKey) {
                hasKey = true;
                gold -= 20;
                System.out.println("Key obtained from NPC");
                gotKey = true;
            }
        }

        // --- Monster fixed left-right movement ---
        int[] monsterPos = findChar(map, 'M');
        int monsterOldR = -1, monsterOldC = -1;
        boolean monsterMoved = false;
        int newMonsterDir = monsterDir;
        if (monsterPos != null) {
            monsterOldR = monsterPos[0];
            monsterOldC = monsterPos[1];
            int mc = monsterOldC + monsterDir;
            if (mc >= 0 && mc < map[0].length) {
                char dest = map[monsterOldR][mc];
                if (dest == ' ' || dest == '*' || dest == 'P') {
                    // Move monster
                    map[monsterOldR][monsterOldC] = ' ';
                    map[monsterOldR][mc] = 'M';
                    monsterMoved = true;
                    // If monster moves onto player, handle interaction
                    if (monsterOldR == r && mc == c) {
                        if (hasSword) {
                            gold += 10;
                            map[monsterOldR][mc] = ' '; // Remove monster
                            defeatedMonster = true;
                            System.out.println("Monster defeated, total gold: " + gold);
                        } else {
                            health -= 100;
                            System.out.println("Attacked by monster! Health: " + health);
                            if (health <= 0) {
                                // Undo actions for backtracking
                                if (pickedSword)
                                    hasSword = false;
                                if (pickedPickaxe)
                                    hasPickaxe = false;
                                if (minedGold)
                                    map[r][c] = 'G';
                                if (defeatedMonster)
                                    map[r][c] = 'M';
                                if (gotKey)
                                    hasKey = false;
                                // Restore monster
                                map[monsterOldR][monsterOldC] = 'M';
                                map[monsterOldR][mc] = dest;
                                return;
                            }
                        }
                    }
                } else {
                    // Hit obstacle, reverse direction for next move
                    newMonsterDir = -monsterDir;
                }
            } else {
                // Out of bounds, reverse direction for next move, but do not move
                newMonsterDir = -monsterDir;
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
                        && temp != 'G' && temp != 'N' && temp != 'M' && temp != 'W')
                    map[nr][nc] = '*';
                backtrackMap4(map, visited, nr, nc, steps + 1, hasKey, health, hasSword, hasPickaxe, gold,
                        newMonsterDir);
                if (temp != 'E' && temp != 'P' && temp != 'K' && temp != 'L' && temp != 'X'
                        && temp != 'G' && temp != 'N' && temp != 'M' && temp != 'W')
                    map[nr][nc] = ' ';
            }
        }
        // Undo actions for backtracking
        if (pickedSword)
            hasSword = false;
        if (pickedPickaxe)
            hasPickaxe = false;
        if (minedGold)
            map[r][c] = 'G';
        if (defeatedMonster)
            map[monsterOldR][monsterOldC] = 'M';
        if (gotKey)
            hasKey = false;
        // Restore monster position if moved
        if (monsterMoved && monsterPos != null) {
            map[monsterPos[0]][monsterPos[1]] = ' ';
            map[monsterOldR][monsterOldC] = 'M';
        }
    }
    // static void backtrackMap4(char[][] map, boolean[][][][] visited, int r, int
    // c, int steps, boolean hasKey,
    // int health,
    // boolean hasPickaxe, int gold) {
    // tries++;
    // if (health <= 0)
    // return;
    // if (map[r][c] == 'E' && hasKey) {
    // if (steps < minSteps || (steps == minSteps && health > bestHealth)) {
    // minSteps = steps;
    // bestHealth = health;
    // bestMap = new char[map.length][map[0].length];
    // for (int i = 0; i < map.length; i++)
    // bestMap[i] = map[i].clone();
    // }
    // return;
    // }
    // int pickaxeIdx = hasPickaxe ? 1 : 0;
    // if (visited[r][c][pickaxeIdx][gold])
    // return;
    // visited[r][c][pickaxeIdx][gold] = true;

    // boolean pickedPickaxe = false, minedGold = false, gotKey = false;

    // // Pickaxe
    // if (map[r][c] == 'X' && !hasPickaxe) {
    // hasPickaxe = true;
    // pickedPickaxe = true;
    // System.out.println("Pickaxe obtained");
    // }
    // // Gold vein
    // if (map[r][c] == 'G' && hasPickaxe) {
    // gold += 10;
    // map[r][c] = ' '; // Remove gold vein
    // minedGold = true;
    // System.out.println("Gold mined, total gold: " + gold);
    // }
    // // NPC
    // if (map[r][c] == 'N') {
    // System.out.println("At NPC: gold=" + gold + " hasKey=" + hasKey);
    // if (gold >= 10 && !hasKey) {
    // hasKey = true;
    // gold -= 10;
    // System.out.println("Key obtained from NPC");
    // gotKey = true;
    // }
    // }

    // for (int d = 0; d < 4; d++) {
    // int nr = r + dr[d], nc = c + dc[d];
    // if (nr >= 0 && nr < map.length && nc >= 0 && nc < map[0].length &&
    // !FileReader2DArray.isWall(map, nr, nc)) {
    // if (map[nr][nc] == 'E' && !hasKey)
    // continue;
    // char temp = map[nr][nc];
    // if (temp != 'E' && temp != 'P' && temp != 'K' && temp != 'L' && temp != 'X'
    // && temp != 'G' && temp != 'N')
    // map[nr][nc] = '*';
    // backtrackMap4(map, visited, nr, nc, steps + 1, hasKey, health, hasPickaxe,
    // gold);
    // if (temp != 'E' && temp != 'P' && temp != 'K' && temp != 'L' && temp != 'X'
    // && temp != 'G' && temp != 'N')
    // map[nr][nc] = ' ';
    // }
    // }
    // // Undo actions for backtracking
    // if (pickedPickaxe)
    // hasPickaxe = false;
    // if (minedGold)
    // map[r][c] = 'G';
    // if (gotKey)
    // hasKey = false;
    // }

}
