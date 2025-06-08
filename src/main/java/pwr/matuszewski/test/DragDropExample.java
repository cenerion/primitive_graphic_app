package pwr.matuszewski.test;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;

public class DragDropExample extends JFrame {
    public DragDropExample() {
        setTitle("Drag & Drop Obrazków");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new GridLayout(1, 2));

        JPanel sourcePanel = new JPanel();
        sourcePanel.setLayout(new BoxLayout(sourcePanel, BoxLayout.Y_AXIS));
        JPanel dropPanel = new JPanel();
        dropPanel.setLayout(new FlowLayout());
        dropPanel.setBorder(BorderFactory.createTitledBorder("Drop Here"));

        // Obrazki źródłowe
        ImageIcon icon1 = new ImageIcon("obrazek1.jpg");
        ImageIcon icon2 = new ImageIcon("obrazek2.jpg");

        JLabel imgLabel1 = new JLabel(icon1);
        JLabel imgLabel2 = new JLabel(icon2);

        // Dodaj TransferHandler do źródłowych JLabel
        imgLabel1.setTransferHandler(new ValueExportTransferHandler(icon1));
        imgLabel2.setTransferHandler(new ValueExportTransferHandler(icon2));

        // Dodaj mouse listener do uruchamiania drag
        MouseAdapter dragListener = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                JComponent c = (JComponent) e.getSource();
                TransferHandler handler = c.getTransferHandler();
                handler.exportAsDrag(c, e, TransferHandler.COPY);
            }
        };

        imgLabel1.addMouseListener(dragListener);
        imgLabel2.addMouseListener(dragListener);

        sourcePanel.add(imgLabel1);
        sourcePanel.add(imgLabel2);

        // Panel docelowy (drop target)
        dropPanel.setTransferHandler(new ImageImportTransferHandler(dropPanel));

        add(sourcePanel);
        add(dropPanel);

        setVisible(true);
    }

    // Handler do eksportu danych (zródło)
    class ValueExportTransferHandler extends TransferHandler {
        private final ImageIcon image;

        public ValueExportTransferHandler(ImageIcon image) {
            this.image = image;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            return new ImageSelection(image);
        }

        @Override
        public int getSourceActions(JComponent c) {
            return COPY;
        }
    }

    // Handler do importu danych (cel)
    class ImageImportTransferHandler extends TransferHandler {
        private final JPanel panel;

        public ImageImportTransferHandler(JPanel panel) {
            this.panel = panel;
        }

        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.imageFlavor);
        }

        @Override
        public boolean importData(TransferSupport support) {
            try {
                Image img = (Image) support.getTransferable().getTransferData(DataFlavor.imageFlavor);
                JLabel newLabel = new JLabel(new ImageIcon(img));
                panel.add(newLabel);
                panel.revalidate();
                panel.repaint();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    // Obiekt Transferable dla obrazków
    class ImageSelection implements Transferable {
        private final Image image;

        public ImageSelection(ImageIcon icon) {
            this.image = icon.getImage();
        }

        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] { DataFlavor.imageFlavor };
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(DataFlavor.imageFlavor);
        }

        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return image;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DragDropExample::new);
    }
}

