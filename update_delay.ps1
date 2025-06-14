$filePath = "c:\Users\benny\OneDrive\Documents\GitHub\alpro2_proyek\Alpro_proyek\src\MapVisualizer.java"
$content = Get-Content -Path $filePath -Raw
$newContent = $content -replace 'private int delaypersec = 0; // SPEED', 'private int delaypersec = 200; // SPEED - increased to show log movement better'
Set-Content -Path $filePath -Value $newContent

$filePath = "c:\Users\benny\OneDrive\Documents\GitHub\alpro2_proyek\Alpro_proyek\src\App.java"
$content = Get-Content -Path $filePath -Raw

# Find the moveLogs method and add visualization code
$pattern = 'static void moveLogs\(char\[\]\[\] map\) \{[\s\S]*?(?=\}\s*\n\s*static)'
$replacement = @'
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
            char[][] visualMap = copyMap(map);
            visualizer.updateMap(visualMap, tries);
        }
    }
'@

$newContent = $content -replace $pattern, $replacement
Set-Content -Path $filePath -Value $newContent

Write-Output "Files updated successfully!"
