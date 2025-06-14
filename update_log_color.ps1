$filePath = "c:\Users\benny\OneDrive\Documents\GitHub\alpro2_proyek\Alpro_proyek\src\MapVisualizer.java"
$content = Get-Content -Path $filePath -Raw

# Make logs more visually distinct by changing their color
$pattern = "case 'O':\s*return new Color\(139, 69, 19\); // Brown for log"
$replacement = "case 'O':\n                return new Color(165, 42, 42); // Brighter brown for log to make it more visible"

$newContent = $content -replace $pattern, $replacement

Set-Content -Path $filePath -Value $newContent

Write-Output "Log visualization color updated successfully!"
