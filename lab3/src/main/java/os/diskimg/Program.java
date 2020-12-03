package os.diskimg;

import os.diskimg.fat.BootSector;
import os.diskimg.fat.FatType;
import os.diskimg.util.Endianness;
import os.diskimg.util.ObjectMarshaller;

import java.util.ArrayList;
import java.util.List;

public class Program {

    public static void main(String[] args) {
        ObjectMarshaller marshaller = new ObjectMarshaller(Endianness.LITTLE_ENDIAN);

        BootSector bootSector = BootSector.create(FatType.FAT32, 2048);

        byte[] result = marshaller.marshall(bootSector);

        int idx = 0;
        for (byte value : result) {
            System.out.print(idx++ + ": ");
            System.out.print((char) value);
            String hex = Integer.toHexString(Byte.toUnsignedInt(value));
            System.out.println("\t (0x" + hex.substring(hex.length() > 1 ? hex.length() - 2 : 0) + ")");
        }
    }

    public static class ImgStruct {
        public byte[] array = {0x8, 0x9, 0xA};
        private String test = "TEST";
        public int count = 3;
        public Internal internal = new Internal();
    }

    public static class Internal {
        public String someTest = "Internal";
        public byte[] data = {'t', 'e', 's', 't'};
        public ArrayList<String> strings = new ArrayList<>(List.of("test1", "test2", "test3"));
    }
}
