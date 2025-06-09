package pwr.matuszewski;

import pwr.matuszewski.transferables.ImageFileTransferHandler;
import pwr.matuszewski.transferables.ImageTransferHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageList extends JPanel {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Lista obrazów");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 600);

            ImageList lista = new ImageList();
            frame.add(lista);

            frame.setVisible(true);
        });
    }

    private final JPanel listaPanel; // Kontener na elementy
    private final JScrollPane scrollPane;

    public ImageList() {
        setLayout(new BorderLayout());

        listaPanel = new JPanel();
        listaPanel.setLayout(new BoxLayout(listaPanel, BoxLayout.Y_AXIS));
        listaPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        listaPanel.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(listaPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        // Ustaw TransferHandler do obsługi plików
        setTransferHandler(new ImageFileTransferHandler(this));
    }


    public void addImageElement(File imageFile) {
        try {
            BufferedImage img = ImageIO.read(imageFile);
            if (img == null) return;

            // Zmniejsz obrazek do miniatury
            Image scaled = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaled));

            JButton removeButton = new JButton("X");
            removeButton.setMargin(new Insets(2, 8, 2, 8));
            removeButton.setForeground(Color.RED);
            removeButton.setFocusable(false);

            JPanel itemPanel = new JPanel(new BorderLayout(10, 10));
            itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
            itemPanel.setBackground(Color.WHITE);
            itemPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

            itemPanel.add(imageLabel, BorderLayout.WEST);
            itemPanel.add(new JLabel(imageFile.getName()), BorderLayout.CENTER);
            itemPanel.add(removeButton, BorderLayout.EAST);

            // Obsługa przycisku usuwania
            removeButton.addActionListener(e -> {
                listaPanel.remove(itemPanel);
                listaPanel.revalidate();
                listaPanel.repaint();
            });

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
