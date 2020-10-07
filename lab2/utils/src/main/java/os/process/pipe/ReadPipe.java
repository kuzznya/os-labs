package os.process.pipe;

import java.io.InputStream;

public interface ReadPipe extends Pipe {
    InputStream getInputStream();
    void closeRead();
}
