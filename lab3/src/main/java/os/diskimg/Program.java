package os.diskimg;

import os.diskimg.fat.FatImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Program {

    public static void main(String[] args) {
        try {
            File file = File.createTempFile("fatimage", "dmg");
            file.deleteOnExit();
            FatImage image = new FatImage(file, 64L * 1024 * 1024);
            image.save();
            FileInputStream outputStream = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(outputStream);
            int pos = -1;
            while (reader.ready()) {
                int value = reader.read();
                pos++;
                if (value == 0)
                    continue;
                System.out.print(pos++ + ": ");
                System.out.print((char) value);
                String hex = Integer.toHexString(value);
                System.out.println("\t (0x" + hex.substring(hex.length() > 1 ? hex.length() - 2 : 0) + ")");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
