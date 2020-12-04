package os.diskimg.fat;

import os.diskimg.util.Alignable;

public class EmptySector implements Alignable {

    private final int size;

    public EmptySector(int size) {
        this.size = size;
    }

    @Override
    public int alignment() {
        return size;
    }
}
