package os.diskimg.fat;

import os.diskimg.util.Alignable;
import os.diskimg.util.IgnoreField;

public class EmptySector implements Alignable {

    @IgnoreField
    private final int size;

    public EmptySector(int size) {
        this.size = size;
    }

    @Override
    public int alignment() {
        return size;
    }
}
