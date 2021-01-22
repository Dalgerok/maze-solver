package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.NumberFormat;
import java.util.ArrayList;

import static javax.swing.JOptionPane.*;

public class Main extends JPanel implements ActionListener {
    private static final int GAP = 0;
    private static final long INIT_SIZE = 20L;

    private JPanel maze;
    private Maze mazeGrid;

    private JFormattedTextField mazeWidth, mazeHeight, mazeDelay;
    private JRadioButton bfsToggle;
    private JRadioButton mazeGeneratorToggle;
    private JButton generateButton, solveButton;
    private JCheckBox checkBox;
    private JSlider sparseChance;


    public Main(){

        open.addActionListener(actionEvent -> OpenActionPerformed());
        save.addActionListener(actionEvent -> SaveActionPerformed());

        JPanel config = new JPanel();
        config.setLayout(new BoxLayout(config, BoxLayout.X_AXIS));
        config.setBorder(BorderFactory.createTitledBorder("Configuration"));

        mazeHeight = new JFormattedTextField(NumberFormat.getNumberInstance());
        mazeHeight.setValue(INIT_SIZE);
        mazeHeight.setPreferredSize(new Dimension(40, 20));
        mazeWidth = new JFormattedTextField(NumberFormat.getNumberInstance());
        mazeWidth.setValue(INIT_SIZE);
        mazeWidth.setPreferredSize(new Dimension(40, 20));
        mazeDelay = new JFormattedTextField(NumberFormat.getNumberInstance());
        mazeDelay.setValue(50L);
        mazeDelay.setPreferredSize(new Dimension(40, 20));
        JPanel dimensions = new JPanel();
        dimensions.add(new JLabel("Height"));
        dimensions.add(mazeHeight);
        dimensions.add(new JLabel("Width"));
        dimensions.add(mazeWidth);
        dimensions.add(new JLabel("Delay in ms"));
        dimensions.add(mazeDelay);
        config.add(dimensions);

        mazeGeneratorToggle = new JRadioButton("Maze");
        JRadioButton sparseGeneratorToggle = new JRadioButton("Sparse");
        mazeGeneratorToggle.setSelected(true);
        ButtonGroup generatorGroup = new ButtonGroup();
        generatorGroup.add(mazeGeneratorToggle);
        generatorGroup.add(sparseGeneratorToggle);
        sparseChance = new JSlider(JSlider.HORIZONTAL, 0, 100, 33);
        sparseChance.setMajorTickSpacing(50);
        config.add(new JLabel("Grid density"));
        config.add(sparseChance);
        config.add(mazeGeneratorToggle);
        config.add(sparseGeneratorToggle);

        generateButton = new JButton("Generate");
        generateButton.addActionListener(this);
        config.add(generateButton);

        bfsToggle = new JRadioButton("BFS");
        JRadioButton aStarToggle = new JRadioButton("A*");
        ButtonGroup algoGroup = new ButtonGroup();
        bfsToggle.setSelected(true);
        algoGroup.add(bfsToggle);
        algoGroup.add(aStarToggle);
        config.add(bfsToggle);
        config.add(aStarToggle);

        checkBox = new JCheckBox("Animation", true);
        config.add(checkBox);

        solveButton = new JButton("Solve");
        solveButton.addActionListener(this);
        config.add(solveButton);


        setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(config);

        maze = new JPanel(new BorderLayout(0, 0));
        maze.setBorder(BorderFactory.createLineBorder(Color.black));
        mazeGrid = new Maze(INIT_SIZE, INIT_SIZE, mazeGeneratorToggle.isSelected(),
                sparseChance.getValue());
        maze.add(mazeGrid.getGridPanel(), BorderLayout.CENTER);
        add(maze);
    }
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        JButton button = (JButton) actionEvent.getSource();
        if (button == generateButton) {
            mazeGrid = new Maze((long)mazeHeight.getValue(), (long)mazeWidth.getValue(),
                    mazeGeneratorToggle.isSelected(), sparseChance.getValue());
            maze.removeAll();
            maze.revalidate();
            maze.repaint();
            maze.add(mazeGrid.getGridPanel());
            maze.repaint();
        }
        if (button == solveButton) {
            if (bfsToggle.isSelected()) {
                mazeGrid.solveBfs((int)((long)mazeDelay.getValue()), checkBox.isSelected());
            }
            else {
                mazeGrid.solveAStar((int)((long)mazeDelay.getValue()), checkBox.isSelected());
            }
        }
    }

    private static final JFileChooser fileChooser = new JFileChooser();
    private static final JMenu menu = new JMenu();
    private static final JMenuBar menuBar = new JMenuBar();
    private static final JMenuItem save = new JMenuItem();
    private static final JMenuItem open = new JMenuItem();

    private void OpenActionPerformed() {
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String st;
                st = br.readLine();
                if (st == null) {
                    showMessageDialog(null, "First line must be: $Height$ space $Width$", "Error", ERROR_MESSAGE);
                    return;
                }
                String[] s = st.split(" ");
                if (s.length != 2) {
                    showMessageDialog(null, "First line must be: $Height$ space $Width$", "Error", ERROR_MESSAGE);
                    return;
                }
                int height, width;
                try {
                    height = Integer.parseInt(s[0]);
                } catch (NumberFormatException e) {
                    showMessageDialog(null, "First line must be: $Height$ space $Width$", "Error", ERROR_MESSAGE);
                    return;
                }
                try {
                    width = Integer.parseInt(s[1]);
                } catch (NumberFormatException e) {
                    showMessageDialog(null, "First line must be: $Height$ space $Width$", "Error", ERROR_MESSAGE);
                    return;
                }

                ArrayList<Character> q = new ArrayList<>();
                q.add('#');
                q.add('.');
                q.add('S');
                q.add('E');
                char[][] cellGrid = new char[height][width];
                int startCellCnt = 0, endCellCnt = 0;
                for (int i = 0; i < height; i++) {
                    st = br.readLine();
                    if (st == null || st.length() != width) {
                        showMessageDialog(null, "Next $Height$ lines must be of length $Width$", "Error", ERROR_MESSAGE);
                        return;
                    }
                    for (int j = 0; j < width; j++) {
                        cellGrid[i][j] = st.charAt(j);
                        if (!q.contains(cellGrid[i][j])) {
                            showMessageDialog(null, "Path: '.', Wall: '#', Start: 'S', End: 'E' ", "Error", ERROR_MESSAGE);
                            return;
                        }
                        startCellCnt += cellGrid[i][j] == 'S' ? 1 : 0;
                        endCellCnt += cellGrid[i][j] == 'E' ? 1 : 0;
                    }
                }
                if (startCellCnt != 1 || endCellCnt != 1) {
                    showMessageDialog(null, "Must be only one Start end End cell", "Error", ERROR_MESSAGE);
                    return;
                }
                mazeGrid = new Maze(height, width, cellGrid);
                maze.removeAll();
                maze.revalidate();
                maze.repaint();
                maze.add(mazeGrid.getGridPanel());
                maze.repaint();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void SaveActionPerformed() {
        int returnVal = fileChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                BufferedWriter br = new BufferedWriter(new FileWriter(file));
                br.write(mazeGrid.getHeight() + " " + mazeGrid.getWidth() + "\n");
                String[] grid = mazeGrid.getStringGrid();
                for (int i = 0; i < mazeGrid.getHeight(); i++) {
                    br.write(grid[i] + "\n");
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();

        menu.setText("File");
        save.setText("Save");
        open.setText("Open");

        frame.getContentPane().add(new Main());

        menu.add(save);
        menu.add(open);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setTitle("Maze solving visualization");
        frame.setMinimumSize(new Dimension(1300, 600));
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }
}
