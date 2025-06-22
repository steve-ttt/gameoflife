package gameoflife;

import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class MainApp extends Application {

    private int width = 1024; // Default width
    private int height = 768; // Default height

    private Canvas canvas;
    private GraphicsContext gc;
    private boolean[][] grid; // The game state
    private int cellSize = 5; // Size of each cell in pixels
    private double distribution = 0.7; // Corresponds to a 30% chance of a cell being alive

    // --- Game Loop Control ---
    private double updatesPerSecond = 20.0; // Default simulation speed
    private double updateInterval = 1.0 / updatesPerSecond;
    private double timeSinceLastUpdate = 0.0;

    @Override
    public void start(Stage stage) throws Exception {
        Parameters params = getParameters();
        List<String> rawArgs = params.getRaw();

        if (rawArgs.size() >= 4) {
            try {
                width = Integer.parseInt(rawArgs.get(0));
                height = Integer.parseInt(rawArgs.get(1));
                cellSize = Integer.parseInt(rawArgs.get(2));
                double parsedDistribution = Double.parseDouble(rawArgs.get(3));
                // Clamp distribution to [0, 1] and set the field (fixing a shadowing bug)
                this.distribution = 1.0 - Math.max(0, Math.min(1, parsedDistribution));

                if (rawArgs.size() >= 5) {
                    updatesPerSecond = Double.parseDouble(rawArgs.get(4));
                    if (updatesPerSecond <= 0) {
                        System.err.println("Updates per second must be positive. Using default of 20.");
                        updatesPerSecond = 10.0;
                    }
                    updateInterval = 1.0 / updatesPerSecond;
                }
                System.out.println("Configuration: " + width + "x" + height + ", Cell Size: " + cellSize + ", Live%: " + String.format("%.2f", (1.0 - distribution)) + ", UPS: " + updatesPerSecond);
            } catch (NumberFormatException e) {
                System.err.println("Invalid number format in arguments. Using default values.");
                printUsage();
            }
        } else if (!rawArgs.isEmpty()) {
            System.err.println("Incorrect number of arguments provided. Using default values.");
            printUsage();
        }

        // Create a simple layout pane for the root of the scene
        Pane root = new StackPane();
        // Create the scene, giving it a width and height, as the FXML is no longer defining it
        Scene scene = new Scene(root, width, height);
        
        stage.setTitle("Conway's -- Game of Life");
        stage.setScene(scene);
        stage.show();

        initialiseGameComponents(root);
        startGameLoop();
    }

    private void initialiseGameComponents(Pane root) {
        // The grid dimensions are based on the window size and cell size
        int gridWidth = width / cellSize;
        int gridHeight = height / cellSize;
        grid = new boolean[gridWidth][gridHeight];

        // Create the canvas and add it to the root pane
        canvas = new Canvas(width, height);
        root.getChildren().add(canvas);

        // Get the graphics context for drawing
        gc = canvas.getGraphicsContext2D();

        // Initialize the grid with a random pattern for testing
        initializeGridRandomly();
    }

    private void initializeGridRandomly() {
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                grid[x][y] = Math.random() > distribution; // Start with ~30% live cells
            }
        }
    }

    private void startGameLoop() {
        final long[] lastNanoTime = {System.nanoTime()};

        AnimationTimer timer = new AnimationTimer() {
            // Store liveCount here to retain its value between frames when no update occurs
            private int liveCount = 0;

            @Override
            public void handle(long now) {
                double deltaTime = (now - lastNanoTime[0]) / 1_000_000_000.0;
                lastNanoTime[0] = now;

                timeSinceLastUpdate += deltaTime;

                // Use a while loop to perform updates at a fixed interval.
                // This handles cases where the game might lag and need to catch up.
                while (timeSinceLastUpdate >= updateInterval) {
                    updateGridState();
                    liveCount = getLiveCount(); // Only get count when grid updates
                    timeSinceLastUpdate -= updateInterval;
                }

                drawGrid();

                // Then draw text for live count
                gc.setFill(Color.WHITE);
                gc.fillText("Live Cells: " + liveCount, 10, 20);
                gc.fillText("Cell Size: " + cellSize + "px", 10, 40);
                // display the distribution rounded to 2 decimal places
                gc.fillText("Distribution: " + String.format("%.2f %%", (1.0 - distribution) * 100), 10, 60);
                // Avoid division by zero if deltaTime is 0
                gc.fillText("FPS: " + (deltaTime > 0 ? (int)(1.0 / deltaTime) : "N/A"), 10, 80);
                gc.fillText("Updates/sec: " + String.format("%.1f", updatesPerSecond), 10, 100);
            }
        };
        timer.start();
    }

    private void drawGrid() {
        // Clear the canvas with a background color
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, height);

        // Set the color for live cells
        gc.setFill(Color.LIMEGREEN);

        // Draw each live cell as a filled rectangle
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                if (grid[x][y]) { // If the cell is alive
                    gc.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                }
            }
        }
    }
    
    private void updateGridState() {
        // The grid dimensions are based on the window size and cell size
        int gridWidth = width / cellSize;
        int gridHeight = height / cellSize;
        boolean[][] newGridState = new boolean[gridWidth][gridHeight];
        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                int liveNeighbors = countLiveNeighbors(x, y);
                if (grid[x][y]) { // Cell is currently alive
                    newGridState[x][y] = liveNeighbors == 2 || liveNeighbors == 3; // Survive
                } else { // Cell is currently dead
                    newGridState[x][y] = liveNeighbors == 3; // Reproduce
                }
            }
        }
        grid = newGridState; // Update the grid to the new state
    }
    
    private int countLiveNeighbors(int x, int y) {
        int liveCount = 0;
        int gridWidth = grid.length; // Width of the grid in cells
        int gridHeight = grid[0].length; // Height of the grid in cells
        
        // Check all 8 neighbors
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue; // Skip the cell itself - we only want neighbors
                // Calculate neighbor coordinates
                int nx = x + dx;
                int ny = y + dy;
                // Check bounds to ensure we don't go out of the grid
                if (nx >= 0 && nx < gridWidth && ny >= 0 && ny < gridHeight) {
                    if (grid[nx][ny]) {
                        liveCount++;
                    }
                }
            }
        }
        return liveCount;
    }

    private int getLiveCount() {
        int liveCount = 0;
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                if (grid[x][y]) {
                    liveCount++;
                }
            }
        }
        return liveCount;
    }

    private void printUsage() {
        System.out.println("\nUsage: java -jar gameOfLife.jar [width height cellSize distribution_pct [updates_per_sec]]");
        System.out.println("Example: java -jar gameOfLife.jar 800 600 5 0.3 15\n");
    }


    public static void main(String[] args) {
        launch(args);
    }
    
}