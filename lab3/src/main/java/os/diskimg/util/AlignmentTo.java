package os.diskimg.util;

import lombok.Getter;

public class AlignmentTo {
    @Getter
    @IgnoreField
    private final int position;

    public AlignmentTo(int position) {
        this.position = position;
    }
}
