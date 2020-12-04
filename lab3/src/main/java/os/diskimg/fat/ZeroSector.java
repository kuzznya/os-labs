package os.diskimg.fat;

import os.diskimg.util.AlignmentTo;

import java.util.Random;

public class ZeroSector {
    private final AlignmentTo alignment = new AlignmentTo(440);

    // Drive signature. Unique ID
    private final int id = new Random().nextInt();
    // len: 16
    private final String section1Description = "                ";
    // len: 16
    private final String section2Description = "                ";
    // len: 16
    private final String section3Description = "                ";
    // len: 16
    private final String section4Description = "                ";

    private final byte[] END_OF_SECTOR = {0x55, (byte) 0xAA};
}
