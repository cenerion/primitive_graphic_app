package pwr.matuszewski;

import pwr.matuszewski.transferables.ImageOnCanvaTransferHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

public class CanvasPanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener {
    public java.util.List<ShapeItem> shapes = new ArrayList<>();
    private ShapeItem selectedShape = null;
    private ShapeItem.Handle currentHandle = null;
    private Point2D lastMousePos = null;
    private boolean resizing = false;
    private final int resizeHandleSize = 10;

    JButton rotateButtonPlus;
    JButton rotateButtonMinus;

    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON3) {
            Point2D p = e.getPoint();
            for (ShapeItem shape : shapes.reversed()) {
                if (shape.contains(p)) {
                    selectedShape = null;
                    currentHandle = null;
                    shapes.remove(shape);
                    repaint();
                    return;
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point2D p = e.getPoint();
        for (ShapeItem shape : shapes.reversed()) {
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
        //selectedShape = null;
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

    public void selectedLayerDown(){
        var idx = shapes.indexOf(selectedShape);
        if (idx > 0) {
            shapes.remove(idx);
            shapes.add(idx - 1, selectedShape);
        }
        repaint();
    }

    public void selectedLayerUp(){
        var idx = shapes.indexOf(selectedShape);
        if (idx < shapes.size() - 1) {
            shapes.remove(idx);
            shapes.add(idx + 1, selectedShape);
        }
        repaint();
    }

    public void selectedRotate(float deg){
        if(selectedShape != null){
            selectedShape.rotate(deg);
            repaint();
        }
    }

    public void selectedMove(float dx, float dy){
        if(selectedShape != null){
            selectedShape.move(dx, dy);
            repaint();
        }
    }

    public CanvasPanel(ImageList imageList) {

        rotateButtonPlus = new JButton("Rotate +10deg");
        rotateButtonPlus.addActionListener(this);
        add(rotateButtonPlus);

        rotateButtonMinus = new JButton("Rotate -10deg");
        rotateButtonMinus.addActionListener(this);
        add(rotateButtonMinus);


        setBackground(Color.WHITE);

        addMouseListener(this);
        addMouseMotionListener(this);

        setTransferHandler(new ImageOnCanvaTransferHandler(this, imageList));
    }

    public void addRectangle(int x, int y, int w, int h) {
        shapes.add(new ShapeItem(x, y, w, h));
        repaint();
    }


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
            //this.shapes.getFirst().angle_deg += 10;
            this.shapes.getLast().rotate(10);

//            if(this.shapes.getFirst().angle_deg > 360.){
//                this.shapes.getFirst().angle_deg -= 360.;
//            }
        }
        else if(source == rotateButtonMinus) {
            //this.shapes.getFirst().angle_deg -= 10;
            this.shapes.getLast().rotate(-10);

//            if(this.shapes.getFirst().angle_deg < 0.){
//                this.shapes.getFirst().angle_deg += 360.;
//            }
        }
        repaint();
    }

    // Abstract shape
    static class ShapeItem {
        enum Handle {
            TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
        }

        Rectangle2D rect;
        double pos_x = 0, pos_y = 0;
        double scale_x = 1., scale_y = 1.;
        double angle_deg = 0;
        AffineTransform transform;

        ShapeItem(int x, int y, int w, int h) {
            rect = new Rectangle2D.Double(-w/2., -h/2., w, h);
            transform = new AffineTransform();
            transform.translate(x, y);
            pos_x = x;
            pos_y = y;
        }

        AffineTransform transform(){
            AffineTransform at = new AffineTransform();
            at.translate(pos_x, pos_y);
            at.rotate(Math.toRadians(angle_deg));
            at.scale(scale_x, scale_y);
            //return at;
            return transform;
        }

        void rotate(double deg){
            Shape transformedShape = transform.createTransformedShape(rect);
            Rectangle2D bounds = transformedShape.getBounds2D();
            double centerX = bounds.getCenterX();
            double centerY = bounds.getCenterY();

            AffineTransform rotation = AffineTransform.getRotateInstance(Math.toRadians(deg), centerX, centerY);
            transform.preConcatenate(rotation);
        }

        void move(double dx, double dy){
            pos_x += (int) dx;
            pos_y += (int) dy;
            var mat = AffineTransform.getTranslateInstance(dx, dy);
            transform().preConcatenate(mat);
        }

        void scale(double dx, double dy){
            scale_x = dx;
            scale_y = dy;
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
            var transformedShape  = mat.createTransformedShape(rect);
            Rectangle2D bounds = transformedShape.getBounds2D();

            // Skala w zależności od uchwytu
            double scaleX = 1.0, scaleY = 1.0;
            double width = bounds.getWidth();
            double height = bounds.getHeight();

            switch (handle) {
                case TOP_LEFT:
                    scaleX = 1.0 - dx / width;
                    scaleY = 1.0 - dy / height;
                    break;
                case TOP_RIGHT:
                    scaleX = 1.0 + dx / width;
                    scaleY = 1.0 - dy / height;
                    break;
                case BOTTOM_LEFT:
                    scaleX = 1.0 - dx / width;
                    scaleY = 1.0 + dy / height;
                    break;
                case BOTTOM_RIGHT:
                    scaleX = 1.0 + dx / width;
                    scaleY = 1.0 + dy / height;
                    break;
            }

            // Minimalna wartość skali
            scaleX = Math.max(scaleX, 0.1);
            scaleY = Math.max(scaleY, 0.1);

            // Wyznaczenie punktu przeciwnego (pivot)
            Point2D pivot = null;
            switch (handle) {
                case TOP_LEFT:     pivot = new Point2D.Double(bounds.getMaxX(), bounds.getMaxY()); break;
                case TOP_RIGHT:    pivot = new Point2D.Double(bounds.getMinX(), bounds.getMaxY()); break;
                case BOTTOM_LEFT:  pivot = new Point2D.Double(bounds.getMaxX(), bounds.getMinY()); break;
                case BOTTOM_RIGHT: pivot = new Point2D.Double(bounds.getMinX(), bounds.getMinY()); break;
            }

            // Stworzenie transformacji: przesuwamy -> skalujemy -> cofamy
            AffineTransform resizeTransform = new AffineTransform();
            resizeTransform.translate(pivot.getX(), pivot.getY());
            resizeTransform.scale(scaleX, scaleY);
            resizeTransform.translate(-pivot.getX(), -pivot.getY());

            transform.preConcatenate(resizeTransform);

        }
    }

    public void addImage(int x, int y, Image image) {

    }
    public static class ImageItem extends ShapeItem {
        //BufferedImage buf_img;
        Image buf_img;

        public ImageItem(int x, int y, Image image){
            super(x,y,image.getWidth(null), image.getHeight(null));
            buf_img = image;
            rect.setFrame(0, 0, image.getWidth(null), image.getHeight(null));

        }

        @Override
        void draw(Graphics2D g, int handleSize) {
            var at = transform();
            //var op = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
            //var transformed_shape = op.createCompatibleDestImage(buf_img, null);

            //g.setTransform(at);
            g.drawImage(buf_img, at, null);

            g.setColor(Color.GRAY);
            for (Handle h : Handle.values()) {
                Rectangle2D handle = getHandleRect(h, handleSize);
                g.fill(handle);
            }
        }

        @Override
        Rectangle2D getHandleRect(Handle handle, int size) {
            var bounds = new Rectangle2D.Double();
            bounds.x = bounds.y = 0;
            bounds.width = buf_img.getWidth(null);
            bounds.height = buf_img.getHeight(null);

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
    }
}
