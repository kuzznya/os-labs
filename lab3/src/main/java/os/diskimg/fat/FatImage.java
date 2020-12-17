package os.diskimg.fat;

import os.diskimg.fat.table.Fat32Table;
import os.diskimg.fat.table.FatTable;
import os.diskimg.file.DataCopy;
import os.diskimg.file.FileBacked;
import os.diskimg.util.AlignmentTo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FatImage implements FileBacked {

//    private final PartitionTable partitionTable;
    private final BootSector bootSector;
    private final AlignmentTo bootSectorCopyAlignment;
    private final DataCopy bootSectorCopy;
    private final AlignmentTo fatTableAlignment;
    private final FatTable table;
    private final DataCopy[] tableCopies;
    private final Cluster[] data;


    public FatImage(File backingFile, long size) throws IOException {
        RandomAccessFile file = new RandomAccessFile(backingFile, "rw");

        file.seek(0);
        byte[] buffer = new byte[1024 * 1024];
        for (int i = 0; i < size / 1024 / 1024; i++)
            file.write(buffer);

        bootSector = BootSector.create(file, 0, FatType.FAT32, size);

        bootSectorCopyAlignment = new AlignmentTo(bootSector.getBPB_BytsPerSec() * 6);
        bootSectorCopy = new DataCopy(bootSectorCopyAlignment.getPosition(), bootSector);

        fatTableAlignment = new AlignmentTo(bootSector.getBPB_BytsPerSec() * bootSector.getBPB_RsvdSecCnt());

        int fatSize = fatTableSizeSectors();
        bootSector.setFatTableSizeSectors(fatSize);
        table = new Fat32Table(file, fatTableAlignment.getPosition(), fatSize, bootSector.getBPB_BytsPerSec());
        tableCopies = new DataCopy[bootSector.getBPB_NumFATs() - 1];
        for (int i = 0; i < bootSector.getBPB_NumFATs() - 1; i++) {
            long tableCopyPosition = fatTableAlignment.getPosition() + fatSize * bootSector.getBPB_BytsPerSec();
            tableCopies[i] = new DataCopy(tableCopyPosition, table);
        }

        data = new Cluster[clustersCount()];
        for (int i = 0; i < data.length; i++) {
            long position = (long) fatTableAlignment.getPosition() +
                    (long) fatSize * bootSector.getBPB_NumFATs() +
                    (long) bootSector.getBPB_SecPerClus() * bootSector.getBPB_BytsPerSec() * i;
            data[i] = new Cluster(file, position, bootSector.getBPB_SecPerClus(), bootSector.getBPB_BytsPerSec());
        }
    }

    @Override
    public byte[] load() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void save() {
        bootSector.save();
        bootSectorCopy.save();
        table.save();
        for (DataCopy tableCopy : tableCopies)
            tableCopy.save();
        for (Cluster cluster : data)
            cluster.save();
    }

    @Override
    public void save(long position) {
        save(); // Not correct, but ...
    }

    public int rootDirSectors() {
        return 0; // only for FAT
    }

    public int totalSectors() {
        return bootSector.getBPB_TotSec();
    }

    public int dataSectors() {
        return totalSectors() - (bootSector.getBPB_RsvdSecCnt() +
                (bootSector.getBPB_NumFATs() * bootSector.getFatTableSize()) + rootDirSectors());
    }

    public int clustersCount() {
        return dataSectors() / bootSector.getBPB_SecPerClus();
    }

    public int fatTableSizeSectors() {
        long tmpVal1 = totalSectors() - (bootSector.getBPB_RsvdSecCnt() + rootDirSectors());
        long tmpVal2 = (256 * bootSector.getBPB_SecPerClus()) + bootSector.getBPB_NumFATs();
        if (bootSector.getType() == FatType.FAT32)
            tmpVal2 /= 2;
        return (int) ((tmpVal1 + (tmpVal2 - 1)) / tmpVal2);
    }
}
