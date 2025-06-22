# Conway's Game of Life in JavaFX

A simple, configurable implementation of John Conway's classic cellular automaton, "The Game of Life," built with Java and JavaFX.


![Conway's Game of Life in Action](gol.gif)

---

## Features

-   **Customizable Simulation:** Control the grid dimensions, cell size, initial live cell density, and simulation speed via command-line arguments.
-   **Real-time Rendering:** Uses JavaFX `Canvas` for efficient rendering of the grid.
-   **Stable Game Loop:** Employs a fixed-timestep game loop for consistent update speeds regardless of rendering performance.
-   **Live Stats:** Displays real-time information including Frames Per Second (FPS), live cell count, and other configuration details.
-   **Self-Contained:** Built into a single executable JAR file for easy distribution and execution.

## Prerequisites

-   Java Development Kit (JDK) 17 or higher.

## Building the Project

The project uses the Gradle build system. The included Gradle wrapper (`gradlew`) handles downloading the correct Gradle version, so you don't need to install it manually.

1.  **Clone the repository:**
    ```bash
    git clone <your-repository-url>
    cd gameoflife
    ```

2.  **Build the executable JAR:**
    Run the `shadowJar` task to compile the code and package it with all its dependencies into a single "fat" JAR file.

    *   On macOS/Linux:
        ```bash
        ./gradlew shadowJar
        ```
    *   On Windows:
        ```bash
        gradlew.bat shadowJar
        ```

    The final JAR file will be located in `app/build/libs/`. It will typically be named something like `app-all.jar` or `gameoflife-all.jar`.

## Running the Application

You can run the application from the command line using the `java -jar` command.

### Default Configuration

To run with the default settings (1024x768 window, 5px cells, 30% live cells, 20 updates/sec):

```bash
java -jar app/build/libs/app-all.jar
```
*(Note: You may need to adjust the JAR file name to match the one generated in your `app/build/libs/` directory.)*

### Custom Configuration

You can override the default settings by providing command-line arguments in the following order:

`width` `height` `cellSize` `distribution_pct` `updates_per_sec`

-   `width`: The width of the window in pixels.
-   `height`: The height of the window in pixels.
-   `cellSize`: The size of each cell in pixels.
-   `distribution_pct`: A value between `0.0` and `1.0` representing the percentage of cells that should be initially alive (e.g., `0.3` for 30%).
-   `updates_per_sec`: The number of simulation steps to perform per second.

**Example:**
To run in a 1920x1080 window with 10-pixel cells, a 15% initial distribution, and 30 updates per second:

```bash
java -jar app/build/libs/app-all.jar 1920 1080 10 0.15 30
```

## How It Works

The simulation is driven by an `AnimationTimer` in JavaFX. A fixed-timestep loop ensures that the game logic (`updateGridState`) is called at a consistent rate, defined by `updatesPerSecond`. The grid is rendered to a `Canvas` on every frame.

The rules of the Game of Life are applied in each update step:
1.  **Underpopulation:** A live cell with fewer than two live neighbours dies.
2.  **Survival:** A live cell with two or three live neighbours lives on to the next generation.
3.  **Overpopulation:** A live cell with more than three live neighbours dies.
4.  **Reproduction:** A dead cell with exactly three live neighbours becomes a live cell.

## License

This project is licensed under the MIT License.

---

**MIT License**

Copyright (c) 2024 [Copyright Holder]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.