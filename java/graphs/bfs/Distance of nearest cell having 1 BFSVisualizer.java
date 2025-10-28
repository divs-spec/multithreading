import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Solution extends JPanel {
    private static final int CELL_SIZE = 40;
    private int n = 8, m = 8;
    private int[][] grid = new int[n][m];
    private int[][] dist = new int[n][m];
    private boolean[][] vis = new boolean[n][m];
    private boolean running = false;

    private JTextArea logArea; // To show internal BFS working

    public Solution(JTextArea logArea) {
        this.logArea = logArea;
        setPreferredSize(new Dimension(m * CELL_SIZE, n * CELL_SIZE));
        setBackground(Color.WHITE);

        // Mouse click to toggle 0/1
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

    private void log(String text) {
        SwingUtilities.invokeLater(() -> logArea.append(text + "\n"));
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
                    log("Added source (" + i + "," + j + ") to queue");
                }
            }
        }

        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        // Multithreaded BFS
        new Thread(() -> {
            try {
                while (!q.isEmpty()) {
                    int[] cell = q.poll();
                    int x = cell[0], y = cell[1];
                    log("Processing cell (" + x + "," + y + ") | dist=" + dist[x][y]);
                    repaint();
                    Thread.sleep(200);

                    for (int k = 0; k < 4; k++) {
                        int nx = x + dx[k], ny = y + dy[k];
                        if (nx >= 0 && ny >= 0 && nx < n && ny < m && !vis[nx][ny]) {
                            vis[nx][ny] = true;
                            dist[nx][ny] = dist[x][y] + 1;
                            q.add(new int[]{nx, ny});
                            log("  -> Added neighbor (" + nx + "," + ny + ") dist=" + dist[nx][ny]);
                        }
                    }

                    // show current queue state
                    StringBuilder qState = new StringBuilder("Queue: ");
                    for (int[] a : q) qState.append("(").append(a[0]).append(",").append(a[1]).append(") ");
                    log(qState.toString());
                }
                log("\n✅ BFS Completed!\n");
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
        logArea.setText("");
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
        JFrame frame = new JFrame("BFS Visualization with Working (Multithreaded)");

        JTextArea logArea = new JTextArea(15, 25);
        logArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        Solution panel = new Solution(logArea);

        JButton startBtn = new JButton("▶ Start BFS");
        JButton resetBtn = new JButton("🔁 Reset");

        startBtn.addActionListener(e -> {
            if (!panel.running) {
                panel.logArea.setText("");
                panel.bfsVisual();
            }
        });

        resetBtn.addActionListener(e -> panel.resetGrid());

        JPanel controlPanel = new JPanel();
        controlPanel.add(startBtn);
        controlPanel.add(resetBtn);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(panel, BorderLayout.CENTER);
        leftPanel.add(controlPanel, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(new JLabel("🧠 BFS Internal Working", SwingConstants.CENTER), BorderLayout.NORTH);
        rightPanel.add(scroll, BorderLayout.CENTER);

        frame.setLayout(new GridLayout(1, 2));
        frame.add(leftPanel);
        frame.add(rightPanel);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
