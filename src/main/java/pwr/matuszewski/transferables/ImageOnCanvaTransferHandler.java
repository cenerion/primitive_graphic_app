package pwr.matuszewski.transferables;

import pwr.matuszewski.CanvasPanel;
import pwr.matuszewski.ImageList;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import static pwr.matuszewski.transferables.Common.isImageFile;


public class ImageOnCanvaTransferHandler extends TransferHandler {
    CanvasPanel canvasPanel;
    ImageList imageList;

    public ImageOnCanvaTransferHandler(CanvasPanel canvasPanel, ImageList imageList) {
        this.canvasPanel = canvasPanel;
        this.imageList = imageList;
    }

    @Override
    public boolean canImport(TransferSupport support) {
        return support.isDataFlavorSupported(TransferableImage.TR_IMAGE_FLAVOR)
                || support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
    }

    @Override
    public boolean importData(TransferSupport support) {
        try {
            if (!canImport(support)) return false;
            var yyy = Arrays.asList(support.getDataFlavors());

            if(yyy.contains(TransferableImage.TR_IMAGE_FLAVOR)){
                var image = (Image)support.getTransferable().getTransferData(TransferableImage.TR_IMAGE_FLAVOR);
                var loc = support.getDropLocation().getDropPoint();
                canvasPanel.shapes.add(new CanvasPanel.ImageItem(loc.x, loc.y, (image)));
                canvasPanel.repaint();
            }
            else if (yyy.contains(DataFlavor.javaFileListFlavor)){
                java.util.List<File> files = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                for (File file : files) {
                    if (isImageFile(file)) {

                        BufferedImage img = ImageIO.read(file);
                        if (img == null) continue;

                        imageList.addImageElement(file);
                        var loc = support.getDropLocation().getDropPoint();
                        canvasPanel.shapes.add(new CanvasPanel.ImageItem(loc.x, loc.y, (img)));
                        canvasPanel.repaint();

                    }
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
