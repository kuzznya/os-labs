package os.diskimg.fat;

import os.diskimg.file.FileBacked;

import java.io.RandomAccessFile;

public class Cluster implements FileBacked {

    private final DataSector[] sectors;

    public Cluster(RandomAccessFile file, long position, int clusterSize, int sectorSize) {
        sectors = new DataSector[clusterSize];
        for (int i = 0; i < clusterSize; i++)
            sectors[i] = new DataSector(file, position, sectorSize);
    }

    @Override
    public void save() {
        for (DataSector sector : sectors)
            sector.save();
    }
}
