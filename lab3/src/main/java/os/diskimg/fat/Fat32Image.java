package os.diskimg.fat;

public class Fat32Image {

    private final ZeroSector zeroSector;
    private final BootSector bootSector;
    private final EmptySector[] emptySectors = new EmptySector[4];
    private final ZeroSector zeroSectorCopy;

    public Fat32Image(int size) {
        zeroSector = new ZeroSector();
        bootSector = BootSector.create(FatType.FAT32, size);
        EmptySector empty = new EmptySector(bootSector.getBPB_BytsPerSec());
        for (int i = 0; i < 4; i++)
            emptySectors[i] = empty;
        zeroSectorCopy = zeroSector;
    }
}
