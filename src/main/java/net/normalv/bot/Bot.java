package net.normalv.bot;

import net.normalv.Main;
import net.normalv.util.Tile;

import java.util.Random;

public class Bot {
    private Random random = new Random();

    public void makeMoveSmart() {
    }

    public void makeMoveSimple() {
        Tile[][] field = Main.getMineField();

        for(int y = 0; y < Main.getHeight(); y++) {
            for(int x = 0; x < Main.getWidth(); x++) {
                if(field[y][x].hidden()) continue;
                int hiddenAmount = 0;
                
            }
        }

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
}
