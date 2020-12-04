package os.diskimg.fat;

import os.diskimg.util.AlignmentTo;

public class Fat32Image {

    private final ZeroSector zeroSector;
    private final BootSector bootSector;
    private final AlignmentTo zeroSectorCopyAlignment;
    private final ZeroSector zeroSectorCopy; // 6th sector
    private final AlignmentTo fatTableAlignment;


    public Fat32Image(int size) {
        zeroSector = new ZeroSector();
        bootSector = BootSector.create(FatType.FAT32, size);
        zeroSectorCopyAlignment = new AlignmentTo(bootSector.getBPB_BytsPerSec() * 6);
        zeroSectorCopy = zeroSector;
        fatTableAlignment = new AlignmentTo(bootSector.getBPB_BytsPerSec() * 32);
    }
}
