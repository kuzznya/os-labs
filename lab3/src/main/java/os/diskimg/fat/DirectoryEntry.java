package os.diskimg.fat;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import os.diskimg.util.Len;

import java.io.RandomAccessFile;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class DirectoryEntry extends DataSector {
    // len: 11
    @Len(11)
    private String DIR_Name;
    private byte DIR_Attr;
    private final byte DIR_NTRes = 0;
    // Count of 0.1s (0-199)
    private byte DIR_CrtTimeTenth;
    private byte DIR_CrtTime;
    private byte DIR_LstAccDate;
    private byte DIR_FstClusHI;
    private byte DIR_WrtTime;
    private byte DIR_WrtDate;
    private byte DIR_FstClusLO;
    private byte DIR_FileSize;


    public static final byte ATTR_READ_ONLY = 0x01;
    public static final byte ATTR_HIDDEN = 0x02;
    public static final byte  ATTR_SYSTEM = 0x04;
    public static final byte ATTR_VOLUME_ID = 0x08;
    public static final byte ATTR_DIRECTORY = 0x10;
    public static final byte ATTR_ARCHIVE = 0x20;
    public static final byte ATTR_LONG_NAME = ATTR_READ_ONLY | ATTR_HIDDEN | ATTR_SYSTEM | ATTR_VOLUME_ID;

    public DirectoryEntry(RandomAccessFile file, long position, int size,
                          String DIR_Name,
                          byte DIR_Attr,
                          byte DIR_CrtTimeTenth,
                          byte DIR_CrtTime,
                          byte DIR_LstAccDate,
                          byte DIR_FstClusHI,
                          byte DIR_WrtTime,
                          byte DIR_WrtDate,
                          byte DIR_FstClusLO,
                          byte DIR_FileSize) {
        super(file, position, size);
        this.DIR_Name = DIR_Name;
        this.DIR_Attr = DIR_Attr;
        this.DIR_CrtTimeTenth = DIR_CrtTimeTenth;
        this.DIR_CrtTime = DIR_CrtTime;
        this.DIR_LstAccDate = DIR_LstAccDate;
        this.DIR_FstClusHI = DIR_FstClusHI;
        this.DIR_WrtTime = DIR_WrtTime;
        this.DIR_WrtDate = DIR_WrtDate;
        this.DIR_FstClusLO = DIR_FstClusLO;
        this.DIR_FileSize = DIR_FileSize;
    }
}
