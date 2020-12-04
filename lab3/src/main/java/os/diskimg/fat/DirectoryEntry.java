package os.diskimg.fat;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DirectoryEntry {
    // len: 11
    private final String DIR_Name;
    private final byte DIR_Attr;
    private final byte DIR_NTRes = 0;
    // Count of 0.1s (0-199)
    private final byte DIR_CrtTimeTenth;
    private final byte DIR_CrtTime;
    private final byte DIR_LstAccDate;
    private final byte DIR_FstClusHI;
    private final byte DIR_WrtTime;
    private final byte DIR_WrtDate;
    private final byte DIR_FstClusLO;
    private final byte DIR_FileSize;


    public static final byte ATTR_READ_ONLY = 0x01;
    public static final byte ATTR_HIDDEN = 0x02;
    public static final byte  ATTR_SYSTEM = 0x04;
    public static final byte ATTR_VOLUME_ID = 0x08;
    public static final byte ATTR_DIRECTORY = 0x10;
    public static final byte ATTR_ARCHIVE = 0x20;
    public static final byte ATTR_LONG_NAME = ATTR_READ_ONLY | ATTR_HIDDEN | ATTR_SYSTEM | ATTR_VOLUME_ID;
}
