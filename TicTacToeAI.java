import java.awt.*;
import javax.swing.*;

public class TicTacToeAI extends JFrame {

    private JButton[][] buttons = new JButton[3][3];
    private boolean playerTurn = true;
    private JLabel status;

    private Color winColor = new Color(144, 238, 144);

    public TicTacToeAI() {
        setTitle("Tic Tac Toe AI");
        setSize(420, 520);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initHeader();
        initBoard();
        initStatusAndControls();

        setVisible(true);
    }

    private void initHeader() {
        JLabel header = new JLabel("Tic Tac Toe AI – By Chandini", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setOpaque(true);
        header.setBackground(new Color(33, 47, 60));
        header.setForeground(Color.WHITE);
        add(header, BorderLayout.NORTH);
    }

    private void initBoard() {
        JPanel panel = new JPanel(new GridLayout(3, 3));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Font f = new Font("Segoe UI", Font.BOLD, 48);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                JButton btn = new JButton("");
                btn.setFont(f);
                btn.setFocusPainted(false);
                int r = i, c = j;

                btn.addActionListener(e -> playerMove(r, c));

                buttons[i][j] = btn;
                panel.add(btn);
            }
        }
        add(panel, BorderLayout.CENTER);
    }

    private void initStatusAndControls() {
        JPanel bottomPanel = new JPanel(new BorderLayout());

        status = new JLabel("Your turn (X)", SwingConstants.CENTER);
        status.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton restart = new JButton("RESTART");
        restart.setFont(new Font("Segoe UI", Font.BOLD, 13));
        restart.addActionListener(e -> restartGame());

        bottomPanel.add(status, BorderLayout.CENTER);
        bottomPanel.add(restart, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void playerMove(int r, int c) {
        if (!playerTurn || !buttons[r][c].getText().equals("")) return;

        buttons[r][c].setText("X");
        playerTurn = false;

        if (checkWin("X")) {
            status.setText("You win!");
            highlightWinningLine("X");
            disableBoard();
            return;
        }

        if (isFull()) {
            status.setText("Draw!");
            return;
        }

        status.setText("AI thinking...");
        aiMove();
    }

    private void aiMove() {
        int bestScore = Integer.MIN_VALUE;
        int bestR = -1, bestC = -1;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().equals("")) {
                    buttons[i][j].setText("O");
                    int score = minimax(false);
                    buttons[i][j].setText("");

                    if (score > bestScore) {
                        bestScore = score;
                        bestR = i;
                        bestC = j;
                    }
                }
            }
        }

        buttons[bestR][bestC].setText("O");

        if (checkWin("O")) {
            status.setText("AI wins!");
            highlightWinningLine("O");
            disableBoard();
            return;
        }

        if (isFull()) {
            status.setText("Draw!");
            return;
        }

        playerTurn = true;
        status.setText("Your turn (X)");
    }

    private int minimax(boolean isMax) {
        if (checkWin("O")) return 1;
        if (checkWin("X")) return -1;
        if (isFull()) return 0;

        int best = isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().equals("")) {
                    buttons[i][j].setText(isMax ? "O" : "X");
                    int score = minimax(!isMax);
                    buttons[i][j].setText("");
                    best = isMax ? Math.max(best, score) : Math.min(best, score);
                }
            }
        }
        return best;
    }

    private boolean checkWin(String p) {
        for (int i = 0; i < 3; i++)
            if (buttons[i][0].getText().equals(p) &&
                buttons[i][1].getText().equals(p) &&
                buttons[i][2].getText().equals(p))
                return true;

        for (int j = 0; j < 3; j++)
            if (buttons[0][j].getText().equals(p) &&
                buttons[1][j].getText().equals(p) &&
                buttons[2][j].getText().equals(p))
                return true;

        if (buttons[0][0].getText().equals(p) &&
            buttons[1][1].getText().equals(p) &&
            buttons[2][2].getText().equals(p))
            return true;

        if (buttons[0][2].getText().equals(p) &&
            buttons[1][1].getText().equals(p) &&
            buttons[2][0].getText().equals(p))
            return true;

        return false;
    }

    private void highlightWinningLine(String p) {
        for (int i = 0; i < 3; i++)
            if (buttons[i][0].getText().equals(p) &&
                buttons[i][1].getText().equals(p) &&
                buttons[i][2].getText().equals(p))
                for (int j = 0; j < 3; j++)
                    buttons[i][j].setBackground(winColor);

        for (int j = 0; j < 3; j++)
            if (buttons[0][j].getText().equals(p) &&
                buttons[1][j].getText().equals(p) &&
                buttons[2][j].getText().equals(p))
                for (int i = 0; i < 3; i++)
                    buttons[i][j].setBackground(winColor);

        if (buttons[0][0].getText().equals(p) &&
            buttons[1][1].getText().equals(p) &&
            buttons[2][2].getText().equals(p))
            for (int i = 0; i < 3; i++)
                buttons[i][i].setBackground(winColor);

        if (buttons[0][2].getText().equals(p) &&
            buttons[1][1].getText().equals(p) &&
            buttons[2][0].getText().equals(p)) {
            buttons[0][2].setBackground(winColor);
            buttons[1][1].setBackground(winColor);
            buttons[2][0].setBackground(winColor);
        }
    }

    private boolean isFull() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (buttons[i][j].getText().equals(""))
                    return false;
        return true;
    }

    private void disableBoard() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                buttons[i][j].setEnabled(false);
    }

    private void restartGame() {
        playerTurn = true;
        status.setText("Your turn (X)");

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setEnabled(true);
                buttons[i][j].setBackground(null);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TicTacToeAI::new);
    }
}
