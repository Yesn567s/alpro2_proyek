import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;

public class FileReader2DArray {
    public static char[][] read2DCharMapFromFile(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        int rowCount = 0;
        int colCount = 0;

        // First, count rows and columns
        while ((line = br.readLine()) != null) {
            if (colCount == 0) colCount = line.length();
            rowCount++;
        }
        br.close();

        char[][] map = new char[rowCount][colCount];
        br = new BufferedReader(new FileReader(filename));
        int row = 0;
        while ((line = br.readLine()) != null) {
            for (int col = 0; col < colCount; col++) {
                map[row][col] = line.charAt(col);
            }
            row++;
        }
        br.close();
        return map;
    }

    public static boolean isWall(char[][] map, int row, int col) {
        if (row < 0 || row >= map.length || col < 0 || col >= map[0].length) {
            return true; // Treat out-of-bounds as wall
        }
        // Treat both '#' and 'S' as walls
        return map[row][col] == '#' || map[row][col] == 'S';
    }

    public static void print2DCharMap(char[][] map) {
        for (char[] row : map) {
            for (char c : row) {
                System.out.print(c);
            }
            System.out.println();
        }
    }
}
