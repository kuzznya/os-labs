package os.diskimg.fat;

import os.diskimg.file.FileBacked;

import java.io.RandomAccessFile;

public class Cluster implements FileBacked {

    private final DataSector[] sectors;
    private final int sectorSize;

    public Cluster(RandomAccessFile file, long position, int clusterSize, int sectorSize) {
        sectors = new DataSector[clusterSize];
        for (int i = 0; i < clusterSize; i++)
            sectors[i] = new DataSector(file, position, sectorSize);

        this.sectorSize = sectorSize;
    }

    @Override
    public void save() {
        for (DataSector sector : sectors)
            sector.save();
    }

    @Override
    public void save(long position) {
        for (int i = 0; i < sectors.length; i++)
            sectors[i].save(position + i * sectorSize);
    }
}
