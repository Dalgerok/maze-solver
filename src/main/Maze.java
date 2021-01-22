package main;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import static main.Cell.Type.*;

public class Maze {
    private final int height;
    private final int width;
    private Cell[][] grid;
    private Cell startCell;
    private Cell endCell;
    private final JPanel gridPanel;

    private final static int INF = 1000000000;
    private final static Random rand = new Random();
    private final static int[] dx = {-1, +1, 0, 0},
            dy = {0, 0, -1, +1};
    private List<Integer> perm = Arrays.asList(
            0, 1, 2, 3
    );
    public Maze(int height, int width, char[][] charGrid) {
        this.height = height;
        this.width = width;
        this.grid = new Cell[this.getHeight()][this.getWidth()];
        gridPanel = new JPanel(new GridLayout(this.getHeight(), this.getWidth(), 0, 0));
        gridPanel.setDoubleBuffered(true);
        gridPanel.setBackground(Color.white);
        for (int i = 0; i < this.getHeight(); i++) {
            for (int j = 0; j < this.getWidth(); j++) {
                switch (charGrid[i][j]) {
                    case '#':
                        grid[i][j] = new Cell(i, j, WALL);
                        break;
                    case '.':
                        grid[i][j] = new Cell(i, j, PATH);
                        break;
                    case 'S':
                        grid[i][j] = new Cell(i, j, START);
                        startCell = grid[i][j];
                        break;
                    case 'E':
                        grid[i][j] = new Cell(i, j, END);
                        endCell = grid[i][j];
                        break;
                }
                gridPanel.add(grid[i][j]);
            }
        }
        reset();
    }

    public Maze(long height, long width, boolean isMaze, int sparseChance) {
        this.height = (int)height;
        this.width = (int)width;
        int sx = rand.nextInt(this.getHeight()),
                sy = rand.nextInt(this.getWidth()),
                ex = rand.nextInt(this.getHeight()),
                ey = rand.nextInt(this.getWidth());
        while(sx == ex && sy == ey) {
            ex = rand.nextInt(this.getHeight());
            ey = rand.nextInt(this.getWidth());
        }
        grid = new Cell[this.getHeight()][this.getWidth()];

        if (isMaze) {
            for (int i = 0; i < this.getHeight(); i++) {
                for (int j = 0; j < this.getWidth(); j++) {
                    grid[i][j] = new Cell(i, j, WALL);
                }
            }
            dfsGenerate(sx, sy);
        }
        else {
            for (int i = 1; i < this.getHeight() - 1; i++) {
                for (int j = 1; j < this.getWidth() - 1; j++) {
                    if(rand.nextInt(100) + 1 <= sparseChance) grid[i][j] = new Cell(i, j, WALL);
                    else grid[i][j] = new Cell(i, j, PATH);
                }
            }
        }
        for (int i = 0; i < this.getHeight(); i++) {
            grid[i][0] = new Cell(i, 0, WALL);
            grid[i][this.getWidth() - 1] = new Cell(i, this.getWidth() - 1, WALL);
        }
        for (int i = 0; i < this.getWidth(); i++) {
            grid[0][i] = new Cell(0, i, WALL);
            grid[this.getHeight() - 1][i] = new Cell(0, i, WALL);
        }
        this.startCell = grid[sx][sy];
        this.endCell = grid[ex][ey];
        grid[sx][sy].type = START;
        grid[ex][ey].type = END;
        gridPanel = new JPanel(new GridLayout(this.getHeight(), this.getWidth(), 0, 0));
        gridPanel.setDoubleBuffered(true);
        gridPanel.setBackground(Color.white);

        for (int i = 0; i < this.getHeight(); i++) {
            for (int j = 0; j < this.getWidth(); j++) {
                gridPanel.add(grid[i][j]);
            }
        }
        reset();
    }
    private boolean insideGrid(int x, int y) {
        return 0 <= x && x < this.getHeight() && 0 <= y && y < this.getWidth();
    }
    public void dfsGenerate(int x, int y) {
        Collections.shuffle(perm);
        grid[x][y].type = PATH;
        for(int i = 0; i < 4; i++){
            int pos = perm.get(i);
            int xx = x + 2 * dx[pos],
                    yy = y + 2 * dy[pos];
            if (insideGrid(xx, yy) && grid[xx][yy].type == WALL) {
                grid[x + dx[pos]][y + dy[pos]].type = PATH;
                dfsGenerate(xx, yy);
            }
        }
    }
    private void reset(){
        for (int i = 0; i < this.getHeight(); i++) {
            for (int j = 0; j < this.getWidth(); j++) {
                grid[i][j].setBorder(BorderFactory.createLineBorder(Color.white));
                switch(grid[i][j].type){
                    case START:
                        grid[i][j].setBackground(Color.green);
                        break;
                    case END:
                        grid[i][j].setBackground(Color.red);
                        break;
                    case PATH:
                        grid[i][j].setBackground(Color.white);
                        break;
                    case WALL:
                        grid[i][j].setBackground(Color.black);
                        break;
                }
            }
        }
        gridPanel.paintComponents(gridPanel.getGraphics());
    }
    public void solveBfs(int delay, boolean isAnimated) {
        reset();
        for (int i = 0; i < this.getHeight(); i++) {
            for (int j = 0; j < this.getWidth(); j++) {
                grid[i][j].val = INF;
            }
        }
        ArrayDeque<Cell> q = new ArrayDeque<>();
        q.add(startCell);
        grid[startCell.x][startCell.y].val = 0;
        while (!q.isEmpty()) {
            Cell v = q.pollFirst();
            ArrayDeque<Cell> s = new ArrayDeque<>();
            s.add(v);
            while (!q.isEmpty() && v.val == q.getFirst().val) {
                s.add(q.pollFirst());
            }
            boolean flag = false;
            while(!s.isEmpty()) {
                v = s.pollFirst();
                if (v == endCell) {
                    reset();
                    do {
                        if (v != startCell && v != endCell) {
                            v.setBackground(Color.magenta);
                        }
                        v = v.parent;
                    } while (v != startCell);
                    gridPanel.paintComponents(gridPanel.getGraphics());
                    flag = true;
                    break;
                }
                if (v != startCell) {
                    if (isAnimated) {
                        grid[v.x][v.y].setBackground(Color.blue);
                        grid[v.x][v.y].paint(grid[v.x][v.y].getGraphics());
                    }
                }
                for (int i = 0; i < 4; i++) {
                    int x = v.x + dx[i],
                            y = v.y + dy[i];
                    if (insideGrid(x, y) && grid[x][y].type != WALL &&
                            grid[x][y].val > grid[v.x][v.y].val + 1) {
                        grid[x][y].val = grid[v.x][v.y].val + 1;
                        grid[x][y].parent = v;
                        q.add(grid[x][y]);
                        if (isAnimated && grid[x][y] != startCell && grid[x][y] != endCell) {
                            grid[x][y].setBackground(Color.yellow);
                            grid[x][y].paint(grid[x][y].getGraphics());
                        }
                    }
                }
            }
            if(flag) break;
            if (isAnimated) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void solveAStar(int delay, boolean isAnimated) {
        reset();
        for (int i = 0; i < this.getHeight(); i++) {
            for (int j = 0; j < this.getWidth(); j++) {
                grid[i][j].val = INF;
            }
        }
        startCell.val = 0;
        PriorityQueue<Cell> q = new PriorityQueue<>(20,
                Comparator.comparingInt(a -> (a.val + a.distance(endCell))));
        q.add(startCell);
        while (!q.isEmpty()) {
            Cell v = q.poll();
            ArrayDeque<Cell> s = new ArrayDeque<>();
            s.add(v);
            while (!q.isEmpty() && v.val + v.distance(endCell) ==
                    Objects.requireNonNull(q.peek()).val +
                            Objects.requireNonNull(q.peek()).distance(endCell)) {
                s.add(Objects.requireNonNull(q.poll()));
            }
            boolean flag = false;
            while (!s.isEmpty()) {
                v = s.pollFirst();
                if (v == endCell) {
                    reset();
                    do {
                        if (v != startCell && v != endCell) {
                            v.setBackground(Color.magenta);
                        }
                        v = v.parent;
                    } while (v != startCell);
                    gridPanel.paintComponents(gridPanel.getGraphics());
                    flag = true;
                    break;
                }
                if (v != startCell) {
                    if (isAnimated) {
                        grid[v.x][v.y].setBackground(Color.blue);
                        grid[v.x][v.y].paint(grid[v.x][v.y].getGraphics());
                    }
                }
                for (int i = 0; i < 4; i++) {
                    int x = v.x + dx[i],
                            y = v.y + dy[i];
                    if (insideGrid(x, y) && grid[x][y].type != WALL
                            && grid[x][y].val > v.val + 1) {
                        q.remove(grid[x][y]);
                        grid[x][y].val = v.val + 1;
                        grid[x][y].parent = v;
                        q.add(grid[x][y]);
                        if (isAnimated && grid[x][y] != startCell && grid[x][y] != endCell) {
                            grid[x][y].setBackground(Color.yellow);
                            grid[x][y].paint(grid[x][y].getGraphics());
                        }
                    }
                }
            }
            if(flag) break;
            if (isAnimated) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public JPanel getGridPanel() {
        return gridPanel;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public String[] getStringGrid() {
        String[] s = new String[this.height];
        for (int i = 0; i < this.height; i++) {
            StringBuilder st = new StringBuilder();
            for (int j = 0; j < this.width; j++) {
                Color background = grid[i][j].getBackground();
                if (Color.green.equals(background)) {
                    st.append('S');
                } else if (Color.red.equals(background)) {
                    st.append('E');
                } else if (Color.white.equals(background)) {
                    st.append('.');
                } else if (Color.black.equals(background)) {
                    st.append('#');
                } else if (Color.magenta.equals(background)) {
                    st.append('P');
                }
            }
            s[i] = st.toString();
        }
        return s;
    }
}
