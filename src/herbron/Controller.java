package herbron;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class Controller extends AnimationTimer implements Initializable {

    @FXML private Canvas canvas;
    @FXML private Button playButton;
    @FXML private Button resetButton;

    private int terrainGrid[][]; // x and y

    private int cellH;
    private int cellW;

    private int cursorX;
    private int cursorY;

    private boolean cursorInCanvas = false;

    private boolean runLife = false;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        terrainGrid = new int[64][64];

        cellH = (int) Math.floor(canvas.getHeight() / terrainGrid.length);
        cellW = (int) Math.floor(canvas.getWidth() / terrainGrid[0].length);

        playButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                runLife = !runLife;

                if(runLife)
                {
                    startLife();
                    playButton.setText("Pause");
                }
                else
                {
                    playButton.setText("Resume");
                }
            }
        });

        resetButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                runLife = false;

                playButton.setText("Start");

                terrainGrid = new int[64][64];
            }
        });

        canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                cursorX = (int) Math.floor(event.getX() / cellW);
                cursorY = (int) Math.floor(event.getY() / cellH);

                terrainGrid[cursorX][cursorY] = 1;
            }
        });

        canvas.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                cursorX = (int) Math.floor(event.getX() / cellW);
                cursorY = (int) Math.floor(event.getY() / cellH);
            }
        });

        canvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                terrainGrid[cursorX][cursorY] = (terrainGrid[cursorX][cursorY] + 1) % 2;
            }
        });

        startLife();

        start();
    }

    private void startLife()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(runLife)
                {
                    processLife();

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void drawBackground()
    {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(new Color(0.1, 0.1, 0.1, 1));

        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void drawGrid()
    {
        GraphicsContext gc = canvas.getGraphicsContext2D() ;
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.0);

        for (int x = 0; x < canvas.getWidth(); x += cellH) {
            double x1 = x + 0.5;

            gc.strokeLine(x1, 0, x1, canvas.getHeight());
        }

        for (int y = 0; y < canvas.getHeight(); y += cellW) {
            double y1 = y + 0.5;

            gc.strokeLine(0, y1, canvas.getWidth(), y1);
        }
    }

    private void drawCursor()
    {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.RED);
        if(cursorInCanvas) gc.fillRect(cursorX * cellW, cursorY * cellH, cellW, cellH);
    }

    private void drawCells()
    {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);

        for(int y = 0; y < terrainGrid.length; y++)
        {
            for(int x = 0; x < terrainGrid[0].length; x++)
            {
                if(terrainGrid[x][y] == 1)
                {
                    gc.fillRect(x * cellW, y * cellH, cellW, cellH);
                }
            }
        }
    }

    private void processLife()
    {
        int nextTerrain[][] = new int[terrainGrid.length][terrainGrid.length];

        for(int y = 0; y < terrainGrid.length; y++)
        {
            for(int x = 0; x < terrainGrid[0].length; x++)
            {
                nextTerrain[x][y] = terrainGrid[x][y];

                int neightbourCells = 0;

                //TOP ROW
                if(x - 1 > 0 && y - 1 > 0 && terrainGrid[x - 1][y - 1] == 1)
                {
                    neightbourCells++;
                }

                if(y - 1 > 0 && terrainGrid[x][y - 1] == 1)
                {
                    neightbourCells++;
                }

                if(x + 1 < terrainGrid.length && y - 1 > 0 && terrainGrid[x + 1][y - 1] == 1)
                {
                    neightbourCells++;
                }

                //MIDDLE ROW
                if(x - 1 > 0 && terrainGrid[x - 1][y] == 1)
                {
                    neightbourCells++;
                }

                if(x + 1 < terrainGrid.length && terrainGrid[x + 1][y] == 1)
                {
                    neightbourCells++;
                }

                //BOTTOM ROW
                if(x - 1 > 0 && y + 1 < terrainGrid[0].length && terrainGrid[x - 1][y + 1] == 1)
                {
                    neightbourCells++;
                }

                if(y + 1 < terrainGrid[0].length && terrainGrid[x][y + 1] == 1)
                {
                    neightbourCells++;
                }

                if(x + 1 < terrainGrid.length && y + 1 < terrainGrid[0].length && terrainGrid[x + 1][y + 1] == 1)
                {
                    neightbourCells++;
                }

                if(neightbourCells == 3)
                {
                    nextTerrain[x][y] = 1;
                }
                else
                {
                    if(neightbourCells < 2 || neightbourCells > 3)
                    {
                        nextTerrain[x][y] = 0;
                    }
                }
            }
        }

        terrainGrid = nextTerrain.clone();
    }

    @FXML
    private void mouseInCanvasEvent()
    {
        cursorInCanvas = !cursorInCanvas;
    }

    @Override
    public void handle(long now) {
        drawBackground();
        drawCursor();
        drawGrid();

        drawCells();
    }
}
