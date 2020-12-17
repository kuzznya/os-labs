package os.diskimg.fat;

import os.diskimg.file.FileBackedData;
import os.diskimg.util.Endianness;
import os.diskimg.util.IgnoreField;
import os.diskimg.util.Marshallable;
import os.diskimg.util.ObjectMarshaller;

import java.io.RandomAccessFile;

public class DataSector extends FileBackedData implements Marshallable {

    private byte[] data;
    @IgnoreField
    private final int size;

    @IgnoreField
    private static final ObjectMarshaller marshaller = new ObjectMarshaller(Endianness.LITTLE_ENDIAN);
    @IgnoreField
    private static final ObjectMarshaller rawMarshaller = new ObjectMarshaller(Endianness.BIG_ENDIAN);

    public DataSector(RandomAccessFile file, long position, int size) {
        super(file, position);
        data = new byte[size];
        save();
        data = null;
        this.size = size;
    }

    public void write(int idx, byte value) {
        super.write(idx, value);
    }

    public void write(int idx, short value) {
        super.write(idx, marshaller.marshall(value));
    }

    public void write(int idx, int value) {
        super.write(idx, marshaller.marshall(value));
    }

    public void writeRaw(int idx, short value) {
        super.write(idx, rawMarshaller.marshall(value));
    }

    public void writeRaw(int idx, int value) {
        super.write(idx, rawMarshaller.marshall(value));
    }

    public void write(int idx, byte[] data) {
        super.write(idx, data);
    }

    public byte read(int idx) {
        return super.read(idx);
    }

    public int readInt(int idx) {
        return super.readInt(idx);
    }

    public byte[] read(int idx, int len) {
        return super.read(idx, len);
    }

    @Override
    public byte[] marshall() {
        return read(0, size);
    }
}
