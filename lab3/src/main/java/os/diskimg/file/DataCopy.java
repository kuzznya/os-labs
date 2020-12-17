package os.diskimg.file;

import lombok.RequiredArgsConstructor;
import os.diskimg.util.IgnoreField;

@RequiredArgsConstructor
public class DataCopy implements FileBacked {
    @IgnoreField
    private final long position;
    private final FileBacked original;


    @Override
    public byte[] load() {
        return original.load();
    }

    @Override
    public void save() {
        System.out.println("Saving data copy of " + original.getClass().getName());
        original.save(position);
    }

    @Override
    public void save(long position) {
        original.save(position);
    }
}
