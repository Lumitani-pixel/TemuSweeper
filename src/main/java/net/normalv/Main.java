package net.normalv;

import net.normalv.bot.Bot;
import net.normalv.util.Tile;
import net.normalv.util.Timer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.Random;

public class Main {
    private static Timer timer = new Timer();
    private static JFrame frame = new JFrame("Temusweeper");
    private static Canvas canvas = new Canvas();

    private static Bot bot = new Bot();

    private static int mineAmount = 99;
    private static int height = 16;
    private static int width = 30;

    private static int tileSize = 20;
    private static int space = 5;

    private static Tile[][] mineField;
    private static Tile[][] bestAttempt;

    private static boolean isRunning = false;
    private static int winCounter = 0;
    private static int lossCounter = 0;
    private static int remainingSafeTiles;
    private static int lowestRemainingSafeTiles = height*width;
    private static int moveCounter = 0;

    // SETTINGS
    private static boolean renderGrid = true;
    private static boolean renderOnlyBestAttempt = true;

    public static void main(String[] args) {
        createMineField();

        canvas.setSize((tileSize+space)*width+space, (tileSize+space)*height+(space*3));
        canvas.setIgnoreRepaint(true);

        frame.add(canvas);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);


        // Create double buffer strategy
        canvas.createBufferStrategy(2);
        Random random = new Random();

        // MAIN LOOP
        while(true) {
            //if(!timer.passedMs(10)) continue;
            isRunning = true;
            render();
            bot.makeMoveSimple();
            //timer.reset();
        }
    }

    private static void render() {
        BufferStrategy strategy = canvas.getBufferStrategy();
        // Render
        Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        int x;
        int y;

        int total = winCounter + lossCounter;

        if(total > 0) {
            double winRatio  = (double) winCounter / total;
            double lossRatio = (double) lossCounter / total;
            int barWidth = (tileSize + space) * width;
            int winWidth  = (int) (barWidth * winRatio);
            int lossWidth = barWidth - winWidth;

            g.setColor(Color.GREEN);
            g.fillRect(space, space*3, winWidth, space);
            g.setColor(Color.RED);
            g.fillRect(space+winWidth, space*3, lossWidth, space);
            g.setColor(Color.BLACK);
            g.drawString("wins: "+winCounter, 150, 12);
            g.drawString("losses: "+lossCounter, canvas.getWidth()-150, 12);
        }

        x = space;
        y = space*5;

        if(renderGrid) {
            for(Tile[] rows : (renderOnlyBestAttempt && bestAttempt != null) ? bestAttempt : mineField) {
                for(Tile tile : rows) {
                    g.setColor(getColor(tile));
                    g.fillRect(x, y, tileSize, tileSize);

                    if(!tile.hidden()) { // Only render text if the tile is visible
                        g.setColor(Color.RED);
                        g.drawString(String.valueOf(tile.value()), x+5, y+15);
                    }

                    x+=(tileSize+space);
                }
                x=space;
                y+=(tileSize+space);
            }
        }

        g.dispose();
        // Display the buffer
        strategy.show();
    }

    private static void reset() {
        createMineField();
        isRunning = false;
        moveCounter = 0;
    }

    private static void createMineField() {
        Random random = new Random();
        mineField = new Tile[height][width];
        remainingSafeTiles = width * height - mineAmount;

        for(int i = 0; i<mineAmount; i++) {
            int y = random.nextInt(height);
            int x = random.nextInt(width);

            while(mineField[y][x] != null) {
                y = random.nextInt(height);
                x = random.nextInt(width);
            }

            mineField[y][x] = new Tile(-1, true, false);
        }

        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {

                if(mineField[y][x] == null || mineField[y][x].value() != -1) mineField[y][x] = new Tile(getMinesAround(y, x), true, false);
            }
        }

        isRunning = true;
    }

    private static int getMinesAround(int y, int x) {
        int mines = 0;

        for(int i = y-1; i <= y+1; i++) {
            if(i<0 || i>=height) continue;

            for(int j = x-1; j <= x+1; j++) {
                if(j<0 || j>=width || (i==y && j==x) || mineField[i][j] == null) continue;
                if(mineField[i][j].value() == -1) mines++;
            }
        }

        return mines;
    }

    public static boolean clickButton(int y, int x) {
        Tile tile = mineField[y][x];

        if (!tile.hidden() || tile.flagged()) return false;

        // If it's a mine
        if (tile.value() == -1) {
            moveCounter++;

            if (remainingSafeTiles < lowestRemainingSafeTiles) {
                lowestRemainingSafeTiles = remainingSafeTiles;
                System.out.println("Lowest Remaining Safe Tiles: " + lowestRemainingSafeTiles);
                bestAttempt = mineField;
            }

            tile.setHidden(false);
            lossCounter++;
            reset();
            return true;
        }

        // Safe tile: reveal it (flood fill)
        revealTile(y, x);
        moveCounter++;

        // Check win condition
        if (remainingSafeTiles == 0) {
            winCounter++;
            bestAttempt = mineField;
            System.out.println("Won with "+moveCounter+" moves");
            reset();
        }

        return true;
    }

    private static void revealTile(int y, int x) {
        if (y < 0 || y >= height || x < 0 || x >= width) return;
        Tile tile = mineField[y][x];

        if (!tile.hidden() || tile.flagged()) return;
        tile.setHidden(false);
        remainingSafeTiles--;

        if (tile.value() == 0) {
            for (int i = y - 1; i <= y + 1; i++) {
                for (int j = x - 1; j <= x + 1; j++) {
                    if (i == y && j == x) continue;
                    revealTile(i, j);
                }
            }
        }
    }

    public static boolean flagButton(int y, int x) {
        if(!mineField[y][x].hidden()) return false;
        moveCounter++;
        mineField[y][x].setFlagged(true);
        return true;
    }

    public static Tile getTile(int y, int x) {
        return mineField[y][x];
    }

    private static Color getColor(Tile tile) {
        if(tile.flagged()) return Color.ORANGE;
        else if(tile.hidden()) return Color.GRAY;
        else if(tile.value() >= 0) return Color.BLUE;
        else return Color.BLACK;
    }

    public static Tile[][] getMineField() {
        return mineField;
    }

    public static int getRemainingSafeTiles() {
        return remainingSafeTiles;
    }

    public static int getMineAmount() {
        return mineAmount;
    }

    public static int getHeight() {
        return height;
    }

    public static int getWidth() {
        return width;
    }

    public static boolean isRunning() {
        return isRunning;
    }
}
