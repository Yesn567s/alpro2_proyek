$filePath = "c:\Users\benny\OneDrive\Documents\GitHub\alpro2_proyek\Alpro_proyek\src\App.java"
$content = Get-Content -Path $filePath -Raw

# Add visualization updates in backtrackMap5 function
$pattern1 = '// Simulate log movement\s*char\[\]\[\] newMap = copyMap\(map\);\s*moveLogs\(newMap\);'
$replacement1 = @'
// Simulate log movement
            char[][] newMap = copyMap(map);
            
            // Display the map before log movement
            char[][] beforeMove = copyMap(newMap);
            visualizer.updateMap(beforeMove, tries, health);
            
            // Now move logs and show the movement
            moveLogs(newMap);
            
            // Display again after log movement with slight delay
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
'@

$newContent = $content -replace $pattern1, $replacement1

# Similarly update backtrackMap6
$pattern2 = '// Simulate log movement\s*char\[\]\[\] newMap = copyMap\(map\);\s*moveLogs\(newMap\);'
$replacement2 = $replacement1

$newContent = $newContent -replace $pattern2, $replacement2

Set-Content -Path $filePath -Value $newContent

Write-Output "Enhanced visualization updates added successfully!"
