package main;

import javax.swing.*;
import java.awt.*;

public class Cell extends JPanel {
    public final int x, y;
    public Type type;
    public Cell parent;
    public int val;

    public Cell(int x, int y, Type type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.val = 0;
        setMinimumSize(new Dimension(0, 0));
    }

    public enum Type{
        WALL, PATH, START, END
    }
    public int distance(Cell to){
        int dx = x - to.x,
                dy = y - to.y;
        return Math.abs(dx) + Math.abs(dy);
    }
}
