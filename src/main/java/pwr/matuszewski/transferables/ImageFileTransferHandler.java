package pwr.matuszewski.transferables;

import pwr.matuszewski.ImageList;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.util.List;

import static pwr.matuszewski.transferables.Common.isImageFile;

public class ImageFileTransferHandler extends TransferHandler {

    ImageList imageList;

    public ImageFileTransferHandler(ImageList imageList) {
        this.imageList = imageList;
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        try {
            if (!canImport(support)) return false;
            List<File> files = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
            for (File file : files) {
                if (isImageFile(file)) {
                    imageList.addImageElement(file);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
