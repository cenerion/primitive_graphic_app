package pwr.matuszewski;

import pwr.matuszewski.transferables.ImageFileTransferHandler;
import pwr.matuszewski.transferables.ImageTransferHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

public class ShapeList extends JPanel {

    private final JPanel listaPanel; // Kontener na elementy
    private final JScrollPane scrollPane;

    public ShapeList() {
        setLayout(new BorderLayout());

        listaPanel = new JPanel();
        listaPanel.setLayout(new BoxLayout(listaPanel, BoxLayout.Y_AXIS));
        listaPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        listaPanel.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(listaPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        var rec = new Rectangle2D.Double(0, 0, 100, 100);
        var ova = new
        addShapeElement(rec, "kwadrat");
    }


    public void addShapeElement(Shape shape, String name) {
        try {
            BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = (Graphics2D)img.getGraphics();
            g2d.setColor(Color.BLACK);
            g2d.fill(shape);

            JLabel imageLabel = new JLabel(new ImageIcon(img));

            JPanel itemPanel = new JPanel(new BorderLayout(10, 10));
            itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
            itemPanel.setBackground(Color.WHITE);
            itemPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

            itemPanel.add(imageLabel, BorderLayout.WEST);
            itemPanel.add(new JLabel(name), BorderLayout.CENTER);


            itemPanel.setTransferHandler(new ImageTransferHandler(img));

            itemPanel.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    JComponent c = (JComponent) e.getSource();
                    TransferHandler handler = c.getTransferHandler();
                    handler.exportAsDrag(c, e, TransferHandler.COPY);
                }
            });

            listaPanel.add(itemPanel);
            listaPanel.revalidate();
            listaPanel.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
