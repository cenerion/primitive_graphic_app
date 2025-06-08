package pwr.matuszewski.transferables;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;

public class ImageTransferHandler extends TransferHandler {
    TransferableImage data;

    public ImageTransferHandler(Image data) {
        super(null);
        this.data = new TransferableImage(data);
        setDragImage(data);
        setDragImageOffset(new Point(0,0));

    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        System.out.println("create Transferable");
        return data;
    }

    @Override
    public int getSourceActions(JComponent c) {

        System.out.println("get Source Actions");
        return COPY;
    }
}
