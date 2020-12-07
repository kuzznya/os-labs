package os.diskimg.fat.table;

import os.diskimg.fat.DataSector;
import os.diskimg.util.IgnoreField;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class Fat32Table implements FatTable {

    private static final int END_OF_CHAIN = 0x0FFFFFFF;
    private static final int BAD_CLUSTER = 0x0FFFFFF7;
    private static final int ROOT_DIR_CLUSTER = 0xFFFFFFF0;

    private final DataSector[] data;
    @IgnoreField
    private final int sectorSize;

    public Fat32Table(RandomAccessFile file, int position, int sectorsCount, int sectorSize) {
        this.sectorSize = sectorSize;

        data = new DataSector[sectorsCount];
        for (int i = 0; i < sectorsCount; i++)
            data[i] = new DataSector(file, position + sectorSize * i, sectorSize);

        setRaw(0, 0x0FFFFFF8);
        setRaw(1, END_OF_CHAIN);
    }

    private int get(int idx) {
        int FATOffset = idx * 4;
        int sectorIdx = FATOffset / sectorSize;
        int entryOffset = FATOffset % sectorSize;
        return data[sectorIdx].readInt(entryOffset);
    }

    private void set(int idx, int value) {
        int FATOffset = idx * 4;
        int sectorIdx = FATOffset / sectorSize;
        int entryOffset = FATOffset % sectorSize;
        data[sectorIdx].write(entryOffset, value);
    }

    private void setRaw(int idx, int value) {
        int FATOffset = idx * 4;
        int sectorIdx = FATOffset / sectorSize;
        int entryOffset = FATOffset % sectorSize;
        data[sectorIdx].writeRaw(entryOffset, value);
    }

    @Override
    public boolean isFree(int idx) {
        if (idx < 2)
            return false;
        return (get(idx) & 0x0FFFFFFF) != 0;
    }

    @Override
    public ClusterChain getFreeChain(int length) {
        List<Integer> result = new ArrayList<>();
        int tablePtr = 2;
        while (result.size() < length) {
            while (!isFree(tablePtr))
                tablePtr++;
            result.add(get(tablePtr));
        }
        return new ClusterChain(this, result);
    }

    @Override
    public void markOccupied(int idx, int previous) {
        set(previous, idx & 0x0FFFFFFF);
        set(idx, END_OF_CHAIN);
    }

    @Override
    public void markFree(int idx) {
        set(idx, 0);
    }

    @Override
    public void save() {
        for (DataSector sector : data)
            sector.save();
    }
}
