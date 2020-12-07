package os.diskimg.fat.table;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

public class ClusterChain {

    private final FatTable table;
    @Getter
    private final List<Integer> clusters;

    ClusterChain(FatTable table, List<Integer> clusters) {
        this.table = table;
        this.clusters = Collections.unmodifiableList(clusters);
    }

    public void occupy() {
        for (int i = 1; i < clusters.size(); i++)
            table.markOccupied(clusters.get(i), clusters.get(i - 1));
    }

    public void release() {
        for (int cluster : clusters)
            table.markFree(cluster);
    }
}
