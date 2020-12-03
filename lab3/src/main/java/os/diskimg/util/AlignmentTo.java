package os.diskimg.util;

import lombok.Getter;

public class AlignmentTo {
    @Getter
    @IgnoreField
    private final int size;

    public AlignmentTo(int size) {
        this.size = size;
    }
}
