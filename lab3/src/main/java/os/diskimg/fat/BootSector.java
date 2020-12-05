package os.diskimg.fat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import os.diskimg.util.AlignmentTo;
import os.diskimg.util.Len;

public class BootSector {

    private final byte[] BS_jmpBoot = {(byte) 0xEB, (byte) 0x3C, (byte) 0x90};

    // manufacturer name, len: 8
    @Len(value = 8, fill = '0')
    private final String BS_OEMName = "kuzznya";
    // Bytes per sector, 512/1024/2048/4096
    @Getter
    private final short BPB_BytsPerSec = 512;

    // Sectors per cluster, 1, 2, 4, 8, ..., but cluster should be less than 32 Kb
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private byte BPB_SecPerClus = 32;

    // Reserved sectors, for FAT 16 often 1, for FAT 32 often 32
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private short BPB_RsvdSecCnt;

    // FAT tables count, often 2
    @Getter
    private final byte BPB_NumFATs = 1;

    // Count of 32-bit records for FAT12/16, 0 for FAT32
    @Setter(AccessLevel.PRIVATE)
    private short BPB_RootEntCount;

    // Count of sectors on disk, for FAT12/16 - count if less than 2^16, else 0, for FAT32 = 0
    @Setter(AccessLevel.PRIVATE)
    private short BPB_TotSec16;

    // Media type, 0xF8 is the standard value for “fixed” (non-removable) media, 0xF0 is frequently used for removable
    private final byte BPB_Media = (byte) 0xF8;

    // for FAT12/16 - FAT table size in sectors, 0 for FAT32
    @Setter(AccessLevel.PRIVATE)
    private short BPB_FATSz16 = 0;

    // Sectors per cylinder
    private final short BPB_SecPerTrk = 32;

    // Count of drive heads
    private final short BPB_NumHeads = 0;

    // Count of hidden sectors before this section
    private final int BPB_HiddSec = 0;

    // Count of sectors on drive, for FAT12/16 only if not in BPB_TotSec16
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private int BPB_TotSec32;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private BPBPart part;

    private final byte[] asmProgram = {
            (byte) 0x0E, (byte) 0x1F, (byte) 0xBE, (byte) 0x77,
            (byte) 0x7C, (byte) 0xAC, (byte) 0x22, (byte) 0xC0,
            (byte) 0x74, (byte) 0x0B, (byte) 0x56, (byte) 0xB4,
            (byte) 0x0E, (byte) 0xBB, (byte) 0x07, (byte) 0x00,
            (byte) 0xCD, (byte) 0x10, (byte) 0x5E, (byte) 0xEB,
            (byte) 0xF0, (byte) 0x32, (byte) 0xE4, (byte) 0xCD,
            (byte) 0xFE, (byte) 0x54, (byte) 0x68, (byte) 0x69,
            0x73, 0x20, 0x69, 0x73,
            0x20, 0x6E, 0x6F, 0x74,
            0x20, 0x61, 0x20, 0x62,
            0x6F, 0x6F, 0x74, 0x61,
            0x62, 0x6C, 0x65, 0x20,
            0x64, 0x69, 0x73, 0x6B,
            0x2E, 0x20, 0x20, 0x50,
            0x6C, 0x65, 0x61, 0x73,
            0x65, 0x20, 0x69, 0x6E,
            0x73, 0x65, 0x72, 0x74,
            0x20, 0x61, 0x20, 0x62,
            0x6F, 0x6F, 0x74, 0x61,
            0x62, 0x6C, 0x65, 0x20,
            0x66, 0x6C, 0x6F, 0x70,
            0x70, 0x79, 0x20, 0x61,
            0x6E, 0x64, 0x0D, 0x0A,
            0x70, 0x72, 0x65, 0x73,
            0x73, 0x20, 0x61, 0x6E,
            0x79, 0x20, 0x6B, 0x65,
            0x79, 0x20, 0x74, 0x6F,
            0x20, 0x74, 0x72, 0x79,
            0x20, 0x61, 0x67, 0x61,
            0x69, 0x6E, 0x20, 0x2E,
            0x2E, 0x2E, 0x20, 0x0D,
            0x0A
    };

    private final AlignmentTo alignment = new AlignmentTo(510);

    private final byte[] END_OF_SECTOR = {0x55, (byte) 0xAA};


    public interface BPBPart { }

    public static class BPBPartFat1216 implements BPBPart {
        // Drive number for BIOS
        private final byte BS_DrvNum = (byte) 0x80;

        private final byte BS_Reserved1 = 0;

        // Extended boot record signature, if has next 3 fields, then 0x29
        private final byte BS_BootSig = 0x29;

        // Volume serial number
        private final int BS_VollD = (int) System.currentTimeMillis();

        // Volume label, len: 11
        @Len(11)
        private final String BS_VolLab = "MYVOLUMELBL";

        // File system name, len: 8, can be "FAT12   ", "FAT16   ", "FAT     "
        @Getter
        @Setter(AccessLevel.PRIVATE)
        @Len(8)
        private String BS_FilSysType;
    }

    public static class BPBPartFat32 implements BPBPart {

        // Sectors per table
        @Getter
        @Setter(AccessLevel.PRIVATE)
        private int BPB_FATSz32;

        // FAT32 flags
        private final short BPB_ExtFlags = 0; // TODO: 04.12.2020 check

        private final short BPB_FSVer = 0;

        // Root dir first cluster number, default is 2
        private final int BPB_RootClus = 2;

        private final short BPB_FSInfo = 1;

        private final short BPB_BkBootSec = 6;

        // reserved, len: 12
        private final byte[] BPB_Reserved = new byte[12];

        private final byte BS_DrvNum = (byte) 0x80;

        private final byte BS_Reserved1 = 0;

        private final byte BS_BootSig = 0x29;

        // Volume serial number
        private final int BS_VollD = (int) System.currentTimeMillis();

        // Volume label, len: 11
        @Len(11)
        private final String BS_VolLab = "MYVOLUMELBL";

        // File system name, len: 8, "FAT32   "
        @Getter
        @Len(8)
        private final String BS_FilSysType = "FAT32   ";

    }

    public int getFatTableSize() {
        if (part instanceof BPBPartFat32)
            return ((BPBPartFat32) part).getBPB_FATSz32();
        else
            return BPB_FATSz16;
    }

    public void setFatTableSize(int size) {
        if (part instanceof BPBPartFat32)
            ((BPBPartFat32) part).setBPB_FATSz32(size);
        else
            BPB_FATSz16 = (short) size;
    }


    public static BootSector create(FatType type, int driveSize) {
        BootSector sector = new BootSector();
        switch (type) {
            case FAT12:
            case FAT16:
                throw new RuntimeException("Not implemented");
            case FAT32:
                if (driveSize < 260 * 1024 * 1024)
                    sector.setBPB_SecPerClus((byte) 1);
                else if (driveSize < 8 * 1024 * 1024 * 1024)
                    sector.setBPB_SecPerClus((byte) 8);
                else
                    sector.setBPB_SecPerClus((byte) 16);
                // Do not handle drives of size more than 16GB
                sector.setBPB_RsvdSecCnt((short) 32);
                sector.setBPB_RootEntCount((short) 0);
                sector.setBPB_TotSec16((short) 0);
                sector.setBPB_FATSz16((short) 0);
                sector.setBPB_TotSec32(driveSize / sector.BPB_BytsPerSec);

                BPBPartFat32 part = new BPBPartFat32();
                part.setBPB_FATSz32(sector.BPB_TotSec32 / sector.BPB_NumFATs);
                sector.setPart(part);
                break;
        }
        return sector;
    }
}
