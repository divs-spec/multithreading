import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Solution extends JPanel {
    private static final int CELL_SIZE = 40;
    private int n = 10, m = 10;
    private int[][] grid = new int[n][m];
    private int[][] dist = new int[n][m];
    private boolean[][] vis = new boolean[n][m];
    private boolean running = false;

    public Solution() {
        setPreferredSize(new Dimension(m * CELL_SIZE, n * CELL_SIZE));
        setBackground(Color.WHITE);

        // Mouse click toggles between 0 and 1
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (running) return;
                int j = e.getX() / CELL_SIZE;
                int i = e.getY() / CELL_SIZE;
                if (i >= 0 && i < n && j >= 0 && j < m) {
                    grid[i][j] = 1 - grid[i][j];
                    repaint();
                }
            }
        });
    }

    private void bfsVisual() {
        running = true;
        Queue<int[]> q = new LinkedList<>();

        for (int i = 0; i < n; i++) {
            Arrays.fill(dist[i], 0);
            Arrays.fill(vis[i], false);
        }

        // Add all 1's to queue
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (grid[i][j] == 1) {
                    q.add(new int[]{i, j});
                    vis[i][j] = true;
                    dist[i][j] = 0;
                }
            }
        }

        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        new Thread(() -> {
            try {
                while (!q.isEmpty()) {
                    int[] cell = q.poll();
                    int x = cell[0], y = cell[1];
                    repaint();
                    Thread.sleep(150);

                    for (int k = 0; k < 4; k++) {
                        int nx = x + dx[k], ny = y + dy[k];
                        if (nx >= 0 && ny >= 0 && nx < n && ny < m && !vis[nx][ny]) {
                            vis[nx][ny] = true;
                            dist[nx][ny] = dist[x][y] + 1;
                            q.add(new int[]{nx, ny});
                        }
                    }
                }
                repaint();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            running = false;
        }).start();
    }

    private void resetGrid() {
        if (running) return;
        for (int i = 0; i < n; i++) {
            Arrays.fill(grid[i], 0);
            Arrays.fill(dist[i], 0);
            Arrays.fill(vis[i], false);
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (grid[i][j] == 1) {
                    g.setColor(Color.GREEN);
                } else if (vis[i][j]) {
                    int intensity = Math.min(255, dist[i][j] * 40);
                    g.setColor(new Color(255 - intensity, 255 - intensity, 255));
                } else {
                    g.setColor(Color.LIGHT_GRAY);
                }

                g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                g.setColor(Color.BLACK);
                g.drawRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                if (vis[i][j]) g.drawString(String.valueOf(dist[i][j]), j * CELL_SIZE + 15, i * CELL_SIZE + 25);
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Interactive BFS Visualization");
        Solution panel = new Solution();

        JButton startBtn = new JButton("▶ Start BFS");
        JButton resetBtn = new JButton("🔁 Reset");

        startBtn.addActionListener(e -> {
            if (!panel.running) panel.bfsVisual();
        });

        resetBtn.addActionListener(e -> panel.resetGrid());

        JPanel controls = new JPanel();
        controls.add(startBtn);
        controls.add(resetBtn);

        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        frame.add(controls, BorderLayout.SOUTH);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
