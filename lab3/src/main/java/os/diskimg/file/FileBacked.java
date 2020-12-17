package os.diskimg.file;

import os.diskimg.util.Endianness;
import os.diskimg.util.ObjectMarshaller;

public interface FileBacked {
    default byte[] load() {
        return new ObjectMarshaller(Endianness.LITTLE_ENDIAN).marshall(this);
    }
    void save();
    void save(long position);
}
