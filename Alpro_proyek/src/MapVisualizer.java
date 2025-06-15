import javax.swing.*;
import java.awt.*;

public class MapVisualizer extends JFrame {
    private JPanel mapPanel;
    private JLabel triesLabel;
    private JLabel hpLabel;
    private static final int CELL_SIZE = 30;
    private char[][] currentMap;
    private int tries;
    private int hp;
    private int delaypersec = 0; // SPEED

    public MapVisualizer(char[][] initialMap) {
        this.currentMap = initialMap;
        this.tries = 0;
        setupGUI();
    }

    public MapVisualizer(char[][] initialMap, int initialHp) {
        this.currentMap = initialMap;
        this.tries = 0;
        this.hp = initialHp;
        setupGUIWithHP();
    }

    private void setupGUI() {
        setTitle("Path Finding Visualization");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create map panel
        mapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawMap(g);
            }
        };
        mapPanel.setPreferredSize(new Dimension(
                currentMap[0].length * CELL_SIZE,
                currentMap.length * CELL_SIZE));

        // Create tries label
        triesLabel = new JLabel("Tries: 0");
        triesLabel.setFont(new Font("Arial", Font.BOLD, 16));
        triesLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));        // Add components to main panel
        mainPanel.add(mapPanel, BorderLayout.CENTER);
        mainPanel.add(triesLabel, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);

        // Pack and center the frame
        pack();
        setLocationRelativeTo(null);
    }

    private void setupGUIWithHP() {
        setTitle("Path Finding Visualization");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create map panel
        mapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawMap(g);
            }
        };
        mapPanel.setPreferredSize(new Dimension(
                currentMap[0].length * CELL_SIZE,
                currentMap.length * CELL_SIZE));

        // Create status panel for tries and HP
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Create tries label
        triesLabel = new JLabel("Tries: 0");
        triesLabel.setFont(new Font("Arial", Font.BOLD, 16));
        triesLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Create HP label
        hpLabel = new JLabel("HP: " + hp);
        hpLabel.setFont(new Font("Arial", Font.BOLD, 16));
        hpLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Add labels to status panel
        statusPanel.add(triesLabel);
        statusPanel.add(hpLabel);        // Add components to main panel
        mainPanel.add(mapPanel, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);

        // Pack and center the frame
        pack();
        setLocationRelativeTo(null);
    }    private void drawMap(Graphics g) {
        // Find player position (should be the last marked "*")
        int playerRow = -1, playerCol = -1;

        // First scan to find the player position
        for (int row = 0; row < currentMap.length; row++) {
            for (int col = 0; col < currentMap[0].length; col++) {
                if (currentMap[row][col] == '*') {
                    playerRow = row;
                    playerCol = col;
                }
            }
        }

        // Now draw the map
        for (int row = 0; row < currentMap.length; row++) {
            for (int col = 0; col < currentMap[0].length; col++) {
                int x = col * CELL_SIZE;
                int y = row * CELL_SIZE;

                // Draw cell background
                g.setColor(getCellColor(currentMap[row][col]));
                g.fillRect(x, y, CELL_SIZE, CELL_SIZE);

                // Draw cell border
                g.setColor(Color.GRAY);
                g.drawRect(x, y, CELL_SIZE, CELL_SIZE);

                // Draw cell content
                g.setColor(Color.BLACK);

                // If this is the player position, draw it differently
                if (playerRow == row && playerCol == col && currentMap[row][col] == '*') {
                    g.setColor(new Color(255, 0, 255)); // Magenta background for player
                    g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                    g.setColor(Color.GRAY);
                    g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
                    g.setColor(Color.BLACK);
                }

                g.drawString(String.valueOf(currentMap[row][col]),
                        x + CELL_SIZE / 3,
                        y + 2 * CELL_SIZE / 3);
            }
        }
    }

    private Color getCellColor(char c) {
        switch (c) {
            case '#':
                return Color.DARK_GRAY;
            case 'P':
                return Color.BLUE;
            case 'E':
                return Color.GREEN;
            case 'K':
                return Color.YELLOW;
            case '*':
                return new Color(173, 216, 230); // Light blue for both player position and path
            case '-':
                return new Color(173, 216, 230); // Light blue for path (kept for backward compatibility)
            case 'L':
                return Color.RED;
            case 'S':
                return Color.GRAY;
            case 'M':
                return new Color(139, 69, 19); // Brown
            case 'G':
                return Color.YELLOW;            case 'N':
                return Color.PINK;
            case 'W':
                return Color.ORANGE;
            case 'X':
                return Color.MAGENTA;
            case 'O':
                return new Color(139, 69, 19); // Brown for log
            case 'A':
                return Color.CYAN; // Cyan for water
            case '1':
                return Color.BLUE;
            case '2':
                return new Color(255, 255, 0);
            default:
                return Color.WHITE;
        }
    }

    public void updateMap(char[][] newMap, int currentTries) {
        this.currentMap = newMap;
        this.tries = currentTries;
        triesLabel.setText("Tries: " + tries);
        mapPanel.repaint();

        // Add small delay to make visualization visible
        try {
            Thread.sleep(delaypersec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void updateMap(char[][] newMap, int currentTries, int hp) {
        this.currentMap = newMap;
        this.tries = currentTries;
        triesLabel.setText("Tries: " + tries);
        this.hp = hp;
        hpLabel.setText("HP: " + hp);
        mapPanel.repaint();

        // Add small delay to make visualization visible
        try {
            Thread.sleep(delaypersec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
  
}


