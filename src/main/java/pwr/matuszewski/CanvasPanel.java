package pwr.matuszewski;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.AffineTransformOp;
import java.util.ArrayList;

public class CanvasPanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener {
    private java.util.List<ShapeItem> shapes = new ArrayList<>();
    private ShapeItem selectedShape = null;
    private ShapeItem.Handle currentHandle = null;
    private Point2D lastMousePos = null;
    private boolean resizing = false;
    private final int resizeHandleSize = 10;


    JButton rotateButtonPlus;
    JButton rotateButtonMinus;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Canvas Example");
            CanvasPanel canvas = new CanvasPanel();

            canvas.addRectangle(0, 0, 200, 200);
            //canvas.addCircle(200, 150, 40);

            frame.add(canvas);
            frame.setSize(600, 400);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

        });
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        Point2D p = e.getPoint();
        for (ShapeItem shape : shapes) {
            ShapeItem.Handle handle = shape.getHandleUnderMouse(p, resizeHandleSize);
            if (handle != null) {
                selectedShape = shape;
                resizing = true;
                currentHandle = handle;
                lastMousePos = p;
                return;
            } else if (shape.contains(p)) {
                selectedShape = shape;
                resizing = false;
                currentHandle = null;
                lastMousePos = p;
                return;
            }
        }
        selectedShape = null;
        currentHandle = null;
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        selectedShape = null;
        resizing = false;
        lastMousePos = null;
        currentHandle = null;
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
        if (selectedShape != null && lastMousePos != null) {
            double dx = e.getX() - lastMousePos.getX();
            double dy = e.getY() - lastMousePos.getY();
            if (resizing && currentHandle != null) {
                selectedShape.resize(dx, dy, currentHandle);
            } else {
                selectedShape.move(dx, dy);
            }
            lastMousePos = e.getPoint();
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {}


    public CanvasPanel() {

        rotateButtonPlus = new JButton("Rotate +10deg");
        rotateButtonPlus.addActionListener(this);
        add(rotateButtonPlus);

        rotateButtonMinus = new JButton("Rotate -10deg");
        rotateButtonMinus.addActionListener(this);
        add(rotateButtonMinus);


        setBackground(Color.WHITE);

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void addRectangle(int x, int y, int w, int h) {
        shapes.add(new RectangleItem(x, y, w, h));
        repaint();
    }

//    public void addCircle(int x, int y, int radius) {
//        shapes.add(new CircleItem(x, y, radius));
//        repaint();
//    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (ShapeItem shape : shapes) {
            shape.draw((Graphics2D) g, resizeHandleSize);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        var source = e.getSource();
        if(source == rotateButtonPlus) {
            this.shapes.getFirst().angle_deg += 10;

            if(this.shapes.getFirst().angle_deg > 360.){
                this.shapes.getFirst().angle_deg -= 360.;
            }
        }
        else if(source == rotateButtonMinus) {
            this.shapes.getFirst().angle_deg -= 10;

            if(this.shapes.getFirst().angle_deg < 0.){
                this.shapes.getFirst().angle_deg += 360.;
            }
        }
        repaint();
    }

    // Abstract shape
    abstract static class ShapeItem {
        enum Handle {
            TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
        }

        double pos_x = 0, pos_y = 0;
        double scale_x = 1., scale_y = 1.;
        double angle_deg = 0;

        AffineTransform transform(){
            AffineTransform at = new AffineTransform();
            at.translate(pos_x, pos_y);
            at.rotate(Math.toRadians(angle_deg));
            at.scale(scale_x, scale_y);
            return at;
        }

        void move(double dx, double dy){
            pos_x += (int) dx;
            pos_y += (int) dy;
        }

        void scale(double dx, double dy){
            scale_x = dx;
            scale_y = dy;
        }

        abstract void draw(Graphics2D g, int handleSize);
        abstract boolean contains(Point2D p);
        abstract Handle getHandleUnderMouse(Point2D p, int handleSize);
        abstract void resize(double dx, double dy, Handle handle);
    }


    // Rectangle
    static class RectangleItem extends ShapeItem {
        Rectangle2D rect;

        RectangleItem(int x, int y, int w, int h) {
            rect = new Rectangle2D.Double(-w/2., -w/2., w, h);
            pos_x = x;
            pos_y = y;
        }

        void draw(Graphics2D g, int handleSize) {
            var at = transform();
            var transformed_shape = at.createTransformedShape(rect);
            g.setColor(Color.BLUE);
            g.draw(transformed_shape);

            g.setColor(Color.GRAY);
            for (Handle h : Handle.values()) {
                Rectangle2D handle = getHandleRect(h, handleSize);
                g.fill(handle);
            }
        }

        Rectangle2D getHandleRect(Handle handle, int size) {
            var bounds = rect.getBounds2D();

            var p = new Point2D.Double();
            switch (handle) {
                case TOP_LEFT:
                    p.setLocation(bounds.getMinX(), bounds.getMinY()); break;
                case TOP_RIGHT:
                    p.setLocation(bounds.getMaxX(), bounds.getMinY()); break;
                case BOTTOM_LEFT:
                    p.setLocation(bounds.getMinX(), bounds.getMaxY()); break;
                case BOTTOM_RIGHT:
                    p.setLocation(bounds.getMaxX(), bounds.getMaxY()); break;
            }

            transform().transform(p,p);

            return new Rectangle2D.Double(p.x - (double) size / 2, p.y - (double) size / 2, size, size);
        }

        boolean contains(Point2D p) {
            var at = transform();
            return at.createTransformedShape(rect).contains(p);
        }

        Handle getHandleUnderMouse(Point2D p, int size) {
            var at = transform();

            for (Handle h : Handle.values()) {
                var handle_shape = getHandleRect(h, size);
                if (handle_shape.contains(p)) {
                    return h;
                }
            }
            return null;
        }

        void resize(double dx, double dy, Handle handle) {
            var mat = transform();

            var image_transform = new AffineTransformOp(mat, AffineTransformOp.TYPE_BICUBIC);


            var bounds = mat.createTransformedShape(rect).getBounds();

            var pivot = new Point2D.Double(0,0);

            var w = bounds.getWidth();
            var h = bounds.getHeight();

            var rotator = AffineTransform.getRotateInstance(-Math.toRadians(angle_deg));
            var p = new Point2D.Double(dx,dy);
            rotator.transform(p,p);
            dx = p.getX();
            dy = p.getY();

            double scale_x = 0., scale_y = 0.;

            switch (handle) {
                case TOP_LEFT:
                    scale_x = (w-dx) / w;
                    scale_y = (h-dy) / h;
                    pivot.setLocation(bounds.getMaxX(), bounds.getMaxY());
                    break;
                case TOP_RIGHT:
                    scale_x = (w+dx) / w;
                    scale_y = (h-dy) / h;
                    pivot.setLocation(bounds.getMinX(), bounds.getMaxY());
                    break;
                case BOTTOM_LEFT:
                    scale_x = (w-dx) / w;
                    scale_y = (h+dy) / h;
                    pivot.setLocation(bounds.getMaxX(), bounds.getMinY());
                    break;
                case BOTTOM_RIGHT:
                    scale_x = (w+dx) / w;
                    scale_y = (h+dy) / h;
                    pivot.setLocation(bounds.getMinX(), bounds.getMinY());
                    break;
            }



            var res = new AffineTransform();
            res.translate(pivot.getX(), pivot.getY());
            //res.rotate(Math.toRadians(angle_deg));
            res.scale(scale_x, scale_y);
            //res.rotate(-Math.toRadians(angle_deg));
            res.translate(-pivot.getX(), -pivot.getY());

            mat.preConcatenate(res);

            double[] m = new double[6];
            mat.getMatrix(m);


/*

// rotation z atan2
            this.angle_deg = Math.toDegrees(Math.atan2(m[1], m[0]));

// długości wektorów: to skale*/
            this.scale_x = Math.sqrt(m[0] * m[0] + m[1] * m[1]);
            this.scale_y = Math.sqrt(m[2] * m[2] + m[3] * m[3]);

            this.pos_x = m[4];
            this.pos_y = m[5];


/*
            this.scale_x = Math.max(mat.getScaleX(), 0.1);
            this.scale_y = Math.max(mat.getScaleY(), 0.1);

            this.pos_x = mat.getTranslateX();
            this.pos_y = mat.getTranslateY();
*/

        }
    }


}
