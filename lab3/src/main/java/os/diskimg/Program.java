package os.diskimg;

import os.diskimg.util.ObjectMarshaller;

public class Program {

    public static void main(String[] args) {
        ObjectMarshaller marshaller = new ObjectMarshaller();

        byte[] result = marshaller.marshall(new ImgStruct());

        for (byte value : result) {
            System.out.print((char) value);
            System.out.println("\t (" + value + ")");
        }
    }

    public static class ImgStruct {
        public byte[] array = {0x8, 0x9, 0xA};
        public String test = "TEST";
        public int count = 3;
        public Internal internal = new Internal();
    }

    public static class Internal {
        public String someTest = "Internal";
        public byte[] data = {'t', 'e', 's', 't'};
    }
}
