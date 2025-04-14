package pwr.matuszewski.test;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.*;

class TransformableRect {
    private final Rectangle2D.Double shape;
    private final AffineTransform transform;
    private final int HANDLE_SIZE = 10;

    private Point2D[] handlePoints; // Punkty w lokalnym układzie
    private int activeHandle = -1;

    public TransformableRect(double x, double y, double w, double h) {
        shape = new Rectangle2D.Double(-w / 2, -h / 2, w, h);
        transform = new AffineTransform();
        transform.translate(x, y);
        updateHandles();
    }

    public void draw(Graphics2D g2) {
        AffineTransform old = g2.getTransform();
        g2.transform(transform);

        g2.setColor(new Color(100, 150, 255, 180));
        g2.fill(shape);

        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(2));
        g2.draw(shape);

        drawHandles(g2);

        g2.setTransform(old);
    }

    private void drawHandles(Graphics2D g2) {
        g2.setColor(Color.RED);
        for (Point2D p : handlePoints) {
            g2.fill(new Rectangle2D.Double(p.getX() - HANDLE_SIZE / 2, p.getY() - HANDLE_SIZE / 2,
                    HANDLE_SIZE, HANDLE_SIZE));
        }
    }

    private void updateHandles() {
        double x = shape.getX(), y = shape.getY(), w = shape.getWidth(), h = shape.getHeight();
        handlePoints = new Point2D[]{
                new Point2D.Double(x, y),             // top-left
                new Point2D.Double(x + w, y),         // top-right
                new Point2D.Double(x + w, y + h),     // bottom-right
                new Point2D.Double(x, y + h)          // bottom-left
        };
    }

    public boolean contains(Point2D p) {
        try {
            AffineTransform inverse = transform.createInverse();
            Point2D localPoint = inverse.transform(p, null);
            return shape.contains(localPoint);
        } catch (Exception e) {
            return false;
        }
    }

    public int getHandleAt(Point2D p) {
        try {
            AffineTransform inverse = transform.createInverse();
            Point2D local = inverse.transform(p, null);

            for (int i = 0; i < handlePoints.length; i++) {
                Rectangle2D handle = new Rectangle2D.Double(
                        handlePoints[i].getX() - HANDLE_SIZE / 2,
                        handlePoints[i].getY() - HANDLE_SIZE / 2,
                        HANDLE_SIZE, HANDLE_SIZE);
                if (handle.contains(local)) {
                    activeHandle = i;
                    return i;
                }
            }
        } catch (Exception ignored) {
        }
        return -1;
    }

    public void dragHandle(Point2D start, Point2D end) {
        if (activeHandle == -1) return;

        try {
            AffineTransform inverse = transform.createInverse();
            Point2D localStart = inverse.transform(start, null);
            Point2D localEnd = inverse.transform(end, null);

            double dx = localEnd.getX() - localStart.getX();
            double dy = localEnd.getY() - localStart.getY();

            double scaleX = 1.0 + dx / shape.getWidth();
            double scaleY = 1.0 + dy / shape.getHeight();

            // Skalujemy względem środka
            AffineTransform local = new AffineTransform();
            local.translate(shape.getCenterX(), shape.getCenterY());
            local.scale(scaleX, scaleY);
            local.translate(-shape.getCenterX(), -shape.getCenterY());
            transform.concatenate(local);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void translate(double dx, double dy) {
        transform.translate(dx, dy);
    }

    public void rotate(double deltaDegrees) {
        transform.rotate(Math.toRadians(deltaDegrees));
    }

    public void resetActiveHandle() {
        activeHandle = -1;
    }

    public boolean isHandleActive() {
        return activeHandle != -1;
    }

    public void updateState() {
        updateHandles();
    }
}
