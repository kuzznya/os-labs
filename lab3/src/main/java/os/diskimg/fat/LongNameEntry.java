package os.diskimg.fat;

import lombok.AllArgsConstructor;
import os.diskimg.util.MarshallWithCharset;

@AllArgsConstructor
public class LongNameEntry {
    private final byte LDIR_Ord;
    // len: 5
    @MarshallWithCharset("UTF16")
    private final String LDIR_Name1;
    private final byte LDIR_Attr = 0x0F;
    private final byte LDIR_Type = 0;
    private final byte LDIR_Chksum;
    // len: 5
    @MarshallWithCharset("UTF16")
    private final String LDIR_Name2;
    private final short LDIR_FstClusLO = 0;
    // last 2 chars
    @MarshallWithCharset("UTF16")
    private final String LDIR_Name3;
}
