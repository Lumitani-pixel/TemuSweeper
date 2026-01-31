package net.normalv.bot;

import net.normalv.Main;
import net.normalv.util.Tile;
import net.normalv.util.TilePos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class Bot {
    private Random random = new Random();

    public void makeMoveSmart() {
    }

    public void makeMoveSimple() {
        Tile[][] field = Main.getMineField();
        AtomicBoolean madeMove = new AtomicBoolean(false);

        for(int y = 0; y < Main.getHeight(); y++) {
            for(int x = 0; x < Main.getWidth(); x++) {
                if(field[y][x].hidden() || field[y][x].flagged()) continue;

                List<TilePos> tilePoses = getHiddenNeighbors(field, y, x);
                int flagged = 0;

                for (TilePos tp : tilePoses) {
                    if (tp.tile.flagged()) flagged++;
                }

                if(flagged == field[y][x].value()) {

                    tilePoses.forEach(tilePos -> {
                        if (tilePos.tile.hidden() && !tilePos.tile.flagged()){

                            Main.clickButton(tilePos.y, tilePos.x);
                            madeMove.set(true);
                        }
                    });
                }
                else if(tilePoses.size() == field[y][x].value()) {

                    tilePoses.forEach(tilePos -> {
                        if(tilePos.tile.hidden()) {

                            Main.flagButton(tilePos.y, tilePos.x);
                            madeMove.set(true);
                        }
                    });
                }
            }
        }

        if(madeMove.get()) return;

        // Fallback
        makeMoveRandom();
    }

    public void makeMoveRandom() {
        int y = random.nextInt(Main.getHeight());
        int x = random.nextInt(Main.getWidth());

        while(!Main.getMineField()[y][x].hidden()) {
            y = random.nextInt(Main.getHeight());
            x = random.nextInt(Main.getWidth());
        }

        Main.clickButton(y, x);
    }

    public static List<TilePos> getHiddenNeighbors(Tile[][] mineField, int y, int x) {
        List<TilePos> neighbors = new ArrayList<>();
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;

                int ny = y + dy;
                int nx = x + dx;

                if (ny < 0 || ny >= Main.getHeight() || nx < 0 || nx >= Main.getWidth() || !mineField[ny][nx].hidden()) continue;
                neighbors.add(new TilePos(mineField[ny][nx], ny, nx));
            }
        }
        return neighbors;
    }
}
