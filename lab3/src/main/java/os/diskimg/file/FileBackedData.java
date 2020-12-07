package os.diskimg.file;

import os.diskimg.util.Endianness;
import os.diskimg.util.IgnoreField;
import os.diskimg.util.ObjectMarshaller;

import java.io.IOException;
import java.io.RandomAccessFile;

public abstract class FileBackedData implements FileBacked {

    @IgnoreField
    private final RandomAccessFile file;
    @IgnoreField
    private final long position;
    @IgnoreField
    private static final ObjectMarshaller marshaller = new ObjectMarshaller(Endianness.LITTLE_ENDIAN);

    protected FileBackedData(RandomAccessFile file, long position) {
        this.file = file;
        this.position = position;
    }

    @Override
    public void save() {
        try {
            file.seek(position);
            byte[] data = marshaller.marshall(this);
            if (data.length > 0)
                file.write(data);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected byte read(int relativePosition) {
        try {
            file.seek(position + relativePosition);
            return file.readByte();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected byte[] read(int relativePosition, int len) {
        try {
            file.seek(position + relativePosition);
            byte[] result = new byte[len];
            file.readFully(result);
            return result;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected short readShortRaw(int relativePosition) {
        try {
            file.seek(position + relativePosition);
            return file.readShort();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected short readShort(int relativePosition) {
        short raw = readShortRaw(relativePosition);
        return (short) ((raw & 0xFF00) >> 8 +
                        (raw & 0x00FF) << 8);
    }

    protected int readIntRaw(int relativePosition) {
        try {
            file.seek(position + relativePosition);
            return file.readInt();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected int readInt(int relativePosition) {
        int raw = readIntRaw(relativePosition);
        return (raw & 0xFF000000) >> 24 +
                (raw & 0x00FF0000) >> 8 +
                (raw & 0x0000FF00) << 8 +
                (raw & 0x000000FF) << 24;
    }

    protected long readLongRaw(int relativePosition) {
        try {
            file.seek(position + relativePosition);
            return file.readLong();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected long readLong(int relativePosition) {
        long raw = readLongRaw(relativePosition);
        return (raw & 0xFF00000000000000L) >> 8 * 7 +
                (raw & 0x00FF000000000000L) >> 8 * 5 +
                (raw & 0x0000FF0000000000L) >> 8 * 3 +
                (raw & 0x000000FF00000000L) >> 8 +
                (raw & 0x00000000FF000000L) << 8 +
                (raw & 0x0000000000FF0000L) << 8 * 3 +
                (raw & 0x000000000000FF00L) << 8 * 5 +
                (raw & 0x00000000000000FFL) << 8 * 7;
    }

    public void write(int idx, byte value) {
        try {
            file.seek(position + idx);
            file.write(value);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void write(int idx, byte[] data) {
        try {
            file.seek(position + idx);
            file.write(data);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
