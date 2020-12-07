package os.diskimg.fat;

import os.diskimg.file.FileBackedData;
import os.diskimg.util.AlignmentTo;

import java.io.RandomAccessFile;
import java.util.Random;

public class PartitionTable extends FileBackedData {
    private final AlignmentTo alignment = new AlignmentTo(440);

    // Drive signature. Unique ID
    private final int id = new Random().nextInt();
    private final short something = 0;
    // len: 16
    private final String section1Description = "                "; // TODO: 06.12.2020 create valid description
    // len: 16
    private final String section2Description = "                ";
    // len: 16
    private final String section3Description = "                ";
    // len: 16
    private final String section4Description = "                ";

    private final byte[] END_OF_SECTOR = {0x55, (byte) 0xAA};

    public PartitionTable(RandomAccessFile file, long position) {
        super(file, position);
    }
}
