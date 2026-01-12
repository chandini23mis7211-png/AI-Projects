import java.awt.*;
import javax.swing.*;

public class EightQueensSimulation extends JFrame {

    private JButton[][] board = new JButton[8][8];
    private int[] queens = new int[8];
    private int currentRow = 0;
    private boolean running = false;

    private static final int TOTAL_SOLUTIONS = 92;

    private JTextArea explanation;
    private JLabel[] ruleLabels = new JLabel[12];

    public EightQueensSimulation() {
        setTitle("N Queens Simulation");
        setSize(1300, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initHeader();
        initBoard();
        initExplanationPanel();
        initRulesPanel();
        initControls();

        resetBoard();
        setVisible(true);
    }

    private void initHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 47, 60));

        JLabel title = new JLabel("♛ N Queens Simulation", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);

        JLabel author = new JLabel("By Chandini  ", SwingConstants.RIGHT);
        author.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        author.setForeground(new Color(220, 220, 220));

        header.add(title, BorderLayout.CENTER);
        header.add(author, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    private void initBoard() {
        JPanel boardPanel = new JPanel(new GridLayout(8, 8));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Color light = new Color(240, 240, 240);
        Color dark = new Color(180, 180, 180);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                JButton cell = new JButton();
                cell.setFont(new Font("Segoe UI Symbol", Font.BOLD, 38));
                cell.setEnabled(false);
                cell.setFocusPainted(false);
                cell.setBackground((i + j) % 2 == 0 ? light : dark);
                board[i][j] = cell;
                boardPanel.add(cell);
            }
        }

        add(boardPanel, BorderLayout.CENTER);
    }

    private void initExplanationPanel() {
        explanation = new JTextArea();
        explanation.setEditable(false);
        explanation.setLineWrap(true);
        explanation.setWrapStyleWord(true);
        explanation.setFont(new Font("Verdana", Font.PLAIN, 15));
        explanation.setBorder(BorderFactory.createTitledBorder(
                "Explanation (Learning Backtracking)"));

        JScrollPane scroll = new JScrollPane(explanation);
        scroll.setPreferredSize(new Dimension(400, 650));
        add(scroll, BorderLayout.WEST);
    }

    private void initRulesPanel() {
        JPanel rulesPanel = new JPanel(new GridLayout(13, 1));
        rulesPanel.setBorder(BorderFactory.createTitledBorder("Production Rules"));

        String[] rules = {
            "R1  Place one queen per row",
            "R2  No two queens in same column",
            "R3  No two queens in same diagonal",
            "R4  Check if position is safe",
            "R5  Place queen if safe",
            "R6  Move to next row",
            "R7  If no safe column → backtrack",
            "R8  Remove previous queen",
            "R9  Try next column",
            "R10 Generate next state",
            "R11 Goal state reached",
            "R12 Stop execution"
        };

        for (int i = 0; i < 12; i++) {
            ruleLabels[i] = new JLabel(rules[i]);
            ruleLabels[i].setOpaque(true);
            ruleLabels[i].setBackground(new Color(235, 235, 235));
            rulesPanel.add(ruleLabels[i]);
        }

        add(rulesPanel, BorderLayout.EAST);
    }

    private void initControls() {
        JPanel control = new JPanel();
        control.setBackground(new Color(245, 245, 245));

        JButton start = new JButton("START");
        JButton next = new JButton("NEXT");
        JButton reset = new JButton("RESET");

        Font f = new Font("Segoe UI", Font.BOLD, 14);
        start.setFont(f);
        next.setFont(f);
        reset.setFont(f);

        start.addActionListener(e -> startSimulation());
        next.addActionListener(e -> nextStep());
        reset.addActionListener(e -> resetBoard());

        control.add(start);
        control.add(next);
        control.add(reset);

        add(control, BorderLayout.SOUTH);
    }

    private void startSimulation() {
        running = true;
        explanation.setText(
                "Simulation started.\n\n" +
                "This simulator shows one valid solution of the 8-Queens problem.\n\n" +
                "Total number of solutions = " + TOTAL_SOLUTIONS + ".\n\n" +
                "Click NEXT to proceed step by step."
        );
        highlightRule(1);
    }

    private void resetBoard() {
        running = false;
        currentRow = 0;

        for (int i = 0; i < 8; i++) {
            queens[i] = -1;
            for (int j = 0; j < 8; j++) {
                board[i][j].setText("");
            }
        }

        explanation.setText(
                "Board reset.\n\n" +
                "Total number of solutions for 8-Queens = " + TOTAL_SOLUTIONS + ".\n\n" +
                "Click START to begin."
        );
        clearHighlights();
    }

    private void nextStep() {
        if (!running) return;

        while (true) {
            if (currentRow == 8) {
                explanation.setText(
                        "Solution found.\n\n" +
                        "All 8 queens are placed safely.\n\n" +
                        "This is one of the " + TOTAL_SOLUTIONS + " possible solutions."
                );
                highlightRule(11);
                running = false;
                return;
            }

            boolean placed = false;

            for (int col = queens[currentRow] + 1; col < 8; col++) {
                if (isSafe(currentRow, col)) {
                    queens[currentRow] = col;
                    board[currentRow][col].setText("♛");

                    explanation.setText(
                            "Placed queen at Row " + (currentRow + 1) +
                            ", Column " + (col + 1) + ".\n\n" +
                            "Moving to next row."
                    );

                    highlightRule(5);
                    currentRow++;
                    highlightRule(6);
                    placed = true;
                    return;
                }
            }

            queens[currentRow] = -1;
            currentRow--;

            if (currentRow < 0) {
                explanation.setText("No solution possible.");
                running = false;
                return;
            }

            int col = queens[currentRow];
            board[currentRow][col].setText("");

            explanation.setText(
                    "Backtracking.\n\n" +
                    "Removing queen from Row " + (currentRow + 1) + ".\n" +
                    "Trying next column."
            );

            highlightRule(7);
            return;
        }
    }

    private boolean isSafe(int row, int col) {
        for (int i = 0; i < row; i++) {
            if (queens[i] == col ||
                Math.abs(queens[i] - col) == row - i)
                return false;
        }
        return true;
    }

    private void highlightRule(int r) {
        clearHighlights();
        if (r >= 1 && r <= 12)
            ruleLabels[r - 1].setBackground(Color.YELLOW);
    }

    private void clearHighlights() {
        for (JLabel lbl : ruleLabels)
            lbl.setBackground(new Color(235, 235, 235));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EightQueensSimulation::new);
    }
}

