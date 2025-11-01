//https://www.geeksforgeeks.org/problems/course-schedule/1
/*import javax.swing.*;
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
*/
/*
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

public class MultiThreadedCourseVisualizer extends JFrame {

    private final int n;
    private final int[][] pre;
    private final Map<Integer, List<Integer>> graph = new HashMap<>();
    private final boolean[] visited;
    private final boolean[] onStack;
    private final Stack<Integer> stack = new Stack<>();
    private final NodePanel[] nodes;
    private boolean hasCycle = false;

    private final GraphPanel graphPanel;

    public MultiThreadedCourseVisualizer(int n, int[][] pre) {
        this.n = n;
        this.pre = pre;
        this.visited = new boolean[n];
        this.onStack = new boolean[n];
        this.nodes = new NodePanel[n];
        this.graphPanel = new GraphPanel();

        setTitle("Multithreaded DFS Course Order Visualizer");
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(graphPanel, BorderLayout.CENTER);

        // Build graph
        for (int[] p : pre) {
            graph.computeIfAbsent(p[0], k -> new ArrayList<>()).add(p[1]);
        }

        // Position nodes in a circle layout
        for (int i = 0; i < n; i++) {
            nodes[i] = new NodePanel(i);
            double angle = 2 * Math.PI * i / n;
            nodes[i].x = (int) (400 + 250 * Math.cos(angle));
            nodes[i].y = (int) (300 + 250 * Math.sin(angle));
        }

        setVisible(true);
    }

    // Multithreaded DFS traversal
    private synchronized void dfs(int course) {
        if (onStack[course]) {
            hasCycle = true;
            nodes[course].animateColor(Color.RED);
            repaint();
            return;
        }
        if (visited[course] || hasCycle) return;

        visited[course] = true;
        onStack[course] = true;
        nodes[course].animateColor(Color.YELLOW);
        repaint();
        sleep(500);

        if (graph.containsKey(course)) {
            for (int neighbor : graph.get(course)) {
                dfs(neighbor);
            }
        }

        onStack[course] = false;
        nodes[course].animateColor(Color.GREEN);
        synchronized (stack) {
            stack.push(course);
        }
        repaint();
        sleep(500);
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
            executor.awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArrayList<Integer> res = new ArrayList<>();
        if (hasCycle) return res;
        while (!stack.isEmpty()) res.add(stack.pop());
        return res;
    }

    // ==============================
    // GUI Rendering Section
    // ==============================
    class GraphPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(Color.BLACK);
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2));
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw edges with arrowheads
            g2.setColor(Color.WHITE);
            for (int i = 0; i < n; i++) {
                if (graph.containsKey(i)) {
                    for (int neigh : graph.get(i)) {
                        drawArrow(g2, nodes[i].x + 20, nodes[i].y + 20,
                                nodes[neigh].x + 20, nodes[neigh].y + 20);
                    }
                }
            }

            // Draw nodes
            for (NodePanel node : nodes) node.draw(g2);
        }

        private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
            g2.drawLine(x1, y1, x2, y2);
            double phi = Math.toRadians(30);
            int barb = 12;
            double dy = y2 - y1;
            double dx = x2 - x1;
            double theta = Math.atan2(dy, dx);
            for (int j = 0; j < 2; j++) {
                double rho = theta + (j == 0 ? phi : -phi);
                int x = (int) (x2 - barb * Math.cos(rho));
                int y = (int) (y2 - barb * Math.sin(rho));
                g2.drawLine(x2, y2, x, y);
            }
        }
    }

    class NodePanel {
        int id, x, y;
        Color color = Color.LIGHT_GRAY;

        NodePanel(int id) { this.id = id; }

        void animateColor(Color target) {
            new Thread(() -> {
                float steps = 20f;
                float rStep = (target.getRed() - color.getRed()) / steps;
                float gStep = (target.getGreen() - color.getGreen()) / steps;
                float bStep = (target.getBlue() - color.getBlue()) / steps;

                for (int i = 0; i < steps; i++) {
                    int r = (int) (color.getRed() + rStep * i);
                    int g = (int) (color.getGreen() + gStep * i);
                    int b = (int) (color.getBlue() + bStep * i);
                    color = new Color(r, g, b);
                    repaint();
                    sleep(25);
                }
                color = target;
                repaint();
            }).start();
        }

        void draw(Graphics2D g2) {
            g2.setColor(color);
            g2.fillOval(x, y, 40, 40);
            g2.setColor(Color.WHITE);
            g2.drawOval(x, y, 40, 40);
            g2.drawString(String.valueOf(id), x + 15, y + 25);
        }
    }

    // ==============================
    // MAIN
    // ==============================
    public static void main(String[] args) {
        int n = 6;
        int[][] pre = {
                {2, 3}, {3, 1}, {4, 0}, {5, 2}, {5, 4}
        };

        MultiThreadedCourseVisualizer vis = new MultiThreadedCourseVisualizer(n, pre);
        new Thread(() -> {
            ArrayList<Integer> order = vis.findOrder();
            System.out.println("\nFinal course order: " +
                    (order.isEmpty() ? "Cycle detected" : order));
        }).start();
    }
}
*/
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

public class MultiThreadedCourseVisualizer extends JFrame {

    private final int n;
    private final int[][] pre;
    private final Map<Integer, List<Integer>> graph = new HashMap<>();
    private final boolean[] visited;
    private final boolean[] onStack;
    private final Stack<Integer> stack = new Stack<>();
    private final NodePanel[] nodes;
    private boolean hasCycle = false;

    private final GraphPanel graphPanel;
    private final LegendPanel legendPanel;
    private volatile int activeFrom = -1, activeTo = -1;

    public MultiThreadedCourseVisualizer(int n, int[][] pre) {
        this.n = n;
        this.pre = pre;
        this.visited = new boolean[n];
        this.onStack = new boolean[n];
        this.nodes = new NodePanel[n];
        this.graphPanel = new GraphPanel();
        this.legendPanel = new LegendPanel();

        setTitle("Multithreaded DFS Course Order Visualizer");
        setSize(1050, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(graphPanel, BorderLayout.CENTER);
        add(legendPanel, BorderLayout.SOUTH);

        // Build graph
        for (int[] p : pre) {
            graph.computeIfAbsent(p[0], k -> new ArrayList<>()).add(p[1]);
        }

        // Position nodes around a circle
        for (int i = 0; i < n; i++) {
            nodes[i] = new NodePanel(i);
            double angle = 2 * Math.PI * i / n;
            nodes[i].x = (int) (450 + 250 * Math.cos(angle));
            nodes[i].y = (int) (300 + 250 * Math.sin(angle));
        }

        setVisible(true);
    }

    private synchronized void dfs(int course) {
        if (onStack[course]) {
            hasCycle = true;
            nodes[course].animateColor(Color.RED);
            repaint();
            return;
        }
        if (visited[course] || hasCycle) return;

        visited[course] = true;
        onStack[course] = true;
        nodes[course].animateColor(Color.YELLOW);
        repaint();
        sleep(400);

        if (graph.containsKey(course)) {
            for (int neighbor : graph.get(course)) {
                activeFrom = course;
                activeTo = neighbor;
                repaint();
                sleep(300);
                dfs(neighbor);
                activeFrom = activeTo = -1;
                repaint();
            }
        }

        onStack[course] = false;
        nodes[course].animateColor(Color.GREEN);
        synchronized (stack) { stack.push(course); }
        repaint();
        sleep(300);
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
                    String msg = Thread.currentThread().getName() + " exploring course " + course;
                    System.out.println(msg);
                    legendPanel.addLog(msg);
                    dfs(course);
                }
            });
        }

        executor.shutdown();
        try { executor.awaitTermination(15, TimeUnit.SECONDS); } catch (InterruptedException e) { e.printStackTrace(); }

        ArrayList<Integer> res = new ArrayList<>();
        if (hasCycle) return res;
        while (!stack.isEmpty()) res.add(stack.pop());
        return res;
    }

    // ---------------- GUI Components ----------------
    class GraphPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(Color.BLACK);
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2));
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw edges
            for (int i = 0; i < n; i++) {
                if (graph.containsKey(i)) {
                    for (int neigh : graph.get(i)) {
                        if (i == activeFrom && neigh == activeTo)
                            g2.setColor(Color.ORANGE);
                        else
                            g2.setColor(Color.WHITE);
                        drawArrow(g2, nodes[i].x + 20, nodes[i].y + 20,
                                nodes[neigh].x + 20, nodes[neigh].y + 20);
                    }
                }
            }

            // Draw nodes
            for (NodePanel node : nodes) node.draw(g2);
        }

        private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
            g2.drawLine(x1, y1, x2, y2);
            double phi = Math.toRadians(30);
            int barb = 12;
            double dy = y2 - y1, dx = x2 - x1, theta = Math.atan2(dy, dx);
            for (int j = 0; j < 2; j++) {
                double rho = theta + (j == 0 ? phi : -phi);
                int x = (int) (x2 - barb * Math.cos(rho));
                int y = (int) (y2 - barb * Math.sin(rho));
                g2.drawLine(x2, y2, x, y);
            }
        }
    }

    class NodePanel {
        int id, x, y;
        Color color = Color.LIGHT_GRAY;

        NodePanel(int id) { this.id = id; }

        void animateColor(Color target) {
            new Thread(() -> {
                float steps = 15f;
                float rStep = (target.getRed() - color.getRed()) / steps;
                float gStep = (target.getGreen() - color.getGreen()) / steps;
                float bStep = (target.getBlue() - color.getBlue()) / steps;
                for (int i = 0; i < steps; i++) {
                    int r = (int) (color.getRed() + rStep * i);
                    int g = (int) (color.getGreen() + gStep * i);
                    int b = (int) (color.getBlue() + bStep * i);
                    color = new Color(Math.min(255, Math.max(0, r)),
                            Math.min(255, Math.max(0, g)),
                            Math.min(255, Math.max(0, b)));
                    repaint();
                    sleep(25);
                }
                color = target;
                repaint();
            }).start();
        }

        void draw(Graphics2D g2) {
            g2.setColor(color);
            g2.fillOval(x, y, 40, 40);
            g2.setColor(Color.WHITE);
            g2.drawOval(x, y, 40, 40);
            g2.drawString(String.valueOf(id), x + 15, y + 25);
        }
    }

    // Legend + Log Panel
    class LegendPanel extends JPanel {
        private final JTextArea logArea;

        LegendPanel() {
            setLayout(new BorderLayout());
            setBackground(Color.DARK_GRAY);
            setPreferredSize(new Dimension(100, 120));

            JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
            legend.setBackground(Color.DARK_GRAY);
            legend.add(createLegendBox(Color.LIGHT_GRAY, "Unvisited"));
            legend.add(createLegendBox(Color.YELLOW, "Visiting"));
            legend.add(createLegendBox(Color.GREEN, "Completed"));
            legend.add(createLegendBox(Color.RED, "Cycle"));

            logArea = new JTextArea(3, 40);
            logArea.setEditable(false);
            logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
            JScrollPane scroll = new JScrollPane(logArea);

            add(legend, BorderLayout.NORTH);
            add(scroll, BorderLayout.CENTER);
        }

        private JPanel createLegendBox(Color c, String text) {
            JPanel box = new JPanel();
            box.setBackground(Color.DARK_GRAY);
            box.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 2));
            JPanel colorBox = new JPanel();
            colorBox.setBackground(c);
            colorBox.setPreferredSize(new Dimension(20, 20));
            JLabel label = new JLabel(text);
            label.setForeground(Color.WHITE);
            box.add(colorBox);
            box.add(label);
            return box;
        }

        synchronized void addLog(String msg) {
            SwingUtilities.invokeLater(() -> {
                logArea.append(msg + "\n");
                logArea.setCaretPosition(logArea.getDocument().getLength());
            });
        }
    }

    // MAIN
    public static void main(String[] args) {
        int n = 6;
        int[][] pre = {
                {2, 3}, {3, 1}, {4, 0}, {5, 2}, {5, 4}
        };

        MultiThreadedCourseVisualizer vis = new MultiThreadedCourseVisualizer(n, pre);
        new Thread(() -> {
            ArrayList<Integer> order = vis.findOrder();
            System.out.println("\nFinal course order: " +
                    (order.isEmpty() ? "Cycle detected" : order));
            vis.legendPanel.addLog("Final Order: " + order);
        }).start();
    }
}
