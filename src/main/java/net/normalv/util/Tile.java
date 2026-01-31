package net.normalv.util;

public class Tile {
    private int value;
    private boolean hidden;
    private boolean flagged;

    public Tile(int value, boolean hidden, boolean flagged) {
        this.value = value;
        this.hidden = hidden;
        this.flagged = flagged;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    public int value() {
        return value;
    }

    public boolean hidden() {
        return hidden;
    }

    public boolean flagged() {
        return flagged;
    }
}
