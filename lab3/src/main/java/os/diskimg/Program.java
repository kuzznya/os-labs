package os.diskimg;

import os.diskimg.fat.FatImage;

import java.io.*;

public class Program {

    public static void main(String[] args) {
        File file = new File(System.getProperty("user.dir") + "/fatimage.img");
        if (file.exists())
            file.delete();

        try {
            FatImage image = new FatImage(file, 64L * 1024 * 1024);
            image.save();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try (FileInputStream inputStream = new FileInputStream(file);
                DataInputStream dataStream = new DataInputStream(inputStream)) {
            int pos = -1;
            while (dataStream.available() > 0 && inputStream.available() > 0) {
                int value = dataStream.readUnsignedByte();
                pos++;
                if (value == 0)
                    continue;
                System.out.print(pos + ": ");
                System.out.print((char) value);
                String hex = Integer.toHexString(value);
                System.out.println("\t (0x" + hex + ")");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
