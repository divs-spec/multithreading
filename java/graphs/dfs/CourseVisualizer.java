//https://www.geeksforgeeks.org/problems/course-schedule/1
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

// -------------------------------------------
// DFS + GUI Visualization
// -------------------------------------------
public class MultiThreadedCourseVisualizer extends JFrame {

    private final int n;
    private final int[][] pre;
    private final Map<Integer, List<Integer>> graph = new HashMap<>();
    private final boolean[] visited;
    private final boolean[] onStack;
    private final Stack<Integer> stack = new Stack<>();
    private final NodePanel[] nodes;
    private boolean hasCycle = false;

    // GUI panel for drawing
    private final GraphPanel graphPanel;

    public MultiThreadedCourseVisualizer(int n, int[][] pre) {
        this.n = n;
        this.pre = pre;
        this.visited = new boolean[n];
        this.onStack = new boolean[n];
        this.nodes = new NodePanel[n];
        this.graphPanel = new GraphPanel();

        setTitle("Multithreaded DFS Course Order Visualizer");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(graphPanel, BorderLayout.CENTER);

        // Build graph
        for (int[] p : pre) {
            graph.computeIfAbsent(p[0], k -> new ArrayList<>()).add(p[1]);
        }

        // Create and position nodes visually
        for (int i = 0; i < n; i++) {
            nodes[i] = new NodePanel(i);
            nodes[i].x = (int) (200 + 400 * Math.cos(2 * Math.PI * i / n));
            nodes[i].y = (int) (200 + 200 * Math.sin(2 * Math.PI * i / n));
        }

        setVisible(true);
    }

    private synchronized void dfs(int course) {
        if (onStack[course]) {
            hasCycle = true;
            nodes[course].setColor(Color.RED);
            repaint();
            return;
        }
        if (visited[course] || hasCycle) return;

        visited[course] = true;
        onStack[course] = true;
        nodes[course].setColor(Color.YELLOW);
        repaint();
        sleep(400);

        if (graph.containsKey(course)) {
            for (int neighbor : graph.get(course)) {
                dfs(neighbor);
            }
        }

        onStack[course] = false;
        nodes[course].setColor(Color.GREEN);
        synchronized (stack) {
            stack.push(course);
        }
        repaint();
        sleep(400);
    }

    private void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    public ArrayList<Integer> findOrder() {
        ExecutorService executor = Executors.newFixedThreadPool(Math.min(n, 6));

        for (int i = 0; i < n; i++) {
            int course = i;
            executor.submit(() -> {
                if (!visited[course]) {
                    System.out.println(Thread.currentThread().getName() + " exploring course " + course);
                    dfs(course);
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArrayList<Integer> res = new ArrayList<>();
        if (hasCycle) return res;
        while (!stack.isEmpty()) res.add(stack.pop());
        return res;
    }

    // -------------------------------------------
    // GUI Components
    // -------------------------------------------
    class GraphPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(Color.BLACK);

            // Draw edges
            g.setColor(Color.WHITE);
            for (int i = 0; i < n; i++) {
                if (graph.containsKey(i)) {
                    for (int neigh : graph.get(i)) {
                        int x1 = nodes[i].x + 20, y1 = nodes[i].y + 20;
                        int x2 = nodes[neigh].x + 20, y2 = nodes[neigh].y + 20;
                        g.drawLine(x1, y1, x2, y2);
                    }
                }
            }

            // Draw nodes
            for (NodePanel node : nodes) node.draw(g);
        }
    }

    class NodePanel {
        int id, x, y;
        Color color = Color.LIGHT_GRAY;

        NodePanel(int id) { this.id = id; }

        void setColor(Color c) {
            this.color = c;
            repaint();
        }

        void draw(Graphics g) {
            g.setColor(color);
            g.fillOval(x, y, 40, 40);
            g.setColor(Color.BLACK);
            g.drawOval(x, y, 40, 40);
            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(id), x + 15, y + 25);
        }
    }

    // -------------------------------------------
    // MAIN
    // -------------------------------------------
    public static void main(String[] args) {
        int n = 6;
        int[][] pre = {
                {2, 3}, {3, 1}, {4, 0}, {5, 2}, {5, 4}
        };

        MultiThreadedCourseVisualizer vis = new MultiThreadedCourseVisualizer(n, pre);
        new Thread(() -> {
            ArrayList<Integer> order = vis.findOrder();
            System.out.println("\nFinal course order: " + (order.isEmpty() ? "Cycle detected" : order));
        }).start();
    }
}
