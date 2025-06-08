package pwr.matuszewski.transferables;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class TransferableImage implements Transferable {
    public static final DataFlavor TR_IMAGE_FLAVOR = new DataFlavor(Image.class, "ImageDET");

    private static final DataFlavor[] SUPPORTED_FLAVORS = {TR_IMAGE_FLAVOR};

    private final Image data;

    public TransferableImage(Image data) {
        this.data = data;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return SUPPORTED_FLAVORS.clone(); // defensive copy
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return TR_IMAGE_FLAVOR.equals(flavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (TR_IMAGE_FLAVOR.equals(flavor)) {
            return data;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
}

