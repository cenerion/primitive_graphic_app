package pwr.matuszewski.test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

public class TransformableCanvas extends JPanel {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Transformacje prostokątów");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new TransformableCanvas());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private final List<TransformableRect> rectangles = new ArrayList<>();
    private TransformableRect selected = null;
    private Point2D lastMouse = null;
    private Mode mode = Mode.NONE;

    enum Mode {
        NONE, DRAG, ROTATE, SCALE
    }

    public TransformableCanvas() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));

        // Dodaj prostokąt klikając prawym przyciskiem myszy

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();

                if (SwingUtilities.isRightMouseButton(e)) {
                    addRectangle(e.getPoint());
                    return;
                }

                selected = getHitRectangle(e.getPoint());
                lastMouse = e.getPoint();

                if (selected != null) {
                    int handleIndex = selected.getHandleAt(e.getPoint());
                    if (handleIndex != -1) {
                        mode = Mode.SCALE;
                    } else if (e.isControlDown()) {
                        mode = Mode.ROTATE;
                    } else {
                        mode = Mode.DRAG;
                    }
                } else {
                    mode = Mode.NONE;
                }
            }

            public void mouseReleased(MouseEvent e) {
                mode = Mode.NONE;
                if (selected != null) {
                    selected.resetActiveHandle();
                    selected.updateState();
                }
                selected = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (selected == null || lastMouse == null) return;

                switch (mode) {
                    case DRAG:
                        double dx = e.getX() - lastMouse.getX();
                        double dy = e.getY() - lastMouse.getY();
                        selected.translate(dx, dy);
                        break;
                    case ROTATE:
                        selected.rotate(e.getX() - lastMouse.getX());
                        break;
                    case SCALE:
                        selected.dragHandle(lastMouse, e.getPoint());
                        break;
                }

                lastMouse = e.getPoint();
                repaint();
            }
        });

        // Usuń zaznaczony prostokąt klawiszem Delete
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE && selected != null) {
                    rectangles.remove(selected);
                    selected = null;
                    repaint();
                }
            }
        });

        setFocusable(true);
    }

    private void addRectangle(Point p) {
        TransformableRect rect = new TransformableRect(p.getX(), p.getY(), 100, 60);
        rectangles.add(rect);
        repaint();
    }

    private TransformableRect getHitRectangle(Point2D p) {
        for (int i = rectangles.size() - 1; i >= 0; i--) {
            if (rectangles.get(i).contains(p)) {
                return rectangles.get(i);
            }
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        for (TransformableRect rect : rectangles) {
            rect.draw(g2);
        }
    }
}
