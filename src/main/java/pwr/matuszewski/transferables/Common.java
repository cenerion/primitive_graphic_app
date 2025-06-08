package pwr.matuszewski.transferables;

import java.io.File;

public class Common {
    static public boolean isImageFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png");
    }
}
