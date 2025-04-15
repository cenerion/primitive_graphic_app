package pwr.matuszewski;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class ListaElementow extends JPanel {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Lista obrazów");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 600);

            ListaElementow lista = new ListaElementow();
            frame.add(lista);

            frame.setVisible(true);
        });
    }

    private final JPanel listaPanel; // Kontener na elementy
    private final JScrollPane scrollPane;

    public ListaElementow() {
        setLayout(new BorderLayout());

        listaPanel = new JPanel();
        listaPanel.setLayout(new BoxLayout(listaPanel, BoxLayout.Y_AXIS));
        listaPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        listaPanel.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(listaPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        // Ustaw TransferHandler do obsługi plików
        setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }

            @Override
            public boolean importData(TransferSupport support) {
                try {
                    if (!canImport(support)) return false;
                    List<File> files = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (File file : files) {
                        if (isImageFile(file)) {
                            addImageElement(file);
                        }
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        });
    }

    private boolean isImageFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png");
    }

    private void addImageElement(File imageFile) {
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

            listaPanel.add(itemPanel);
            listaPanel.revalidate();
            listaPanel.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
