package os.diskimg.fat.table;

import os.diskimg.file.FileBacked;

public interface FatTable extends FileBacked {
    boolean isFree(int idx);
    ClusterChain getFreeChain(int length);
    ClusterChain getChain(int start, int length);
    void markOccupied(int idx);
    void markOccupied(int idx, int previous);
    void markFree(int idx);
}
