package os.logger;

import os.memory.SharedMemory;
import os.process.Mutex;
import os.process.Runtime;
import os.process.Signal;

import java.io.IOException;
import java.util.Date;

public class LoggerSender {

    private final SharedMemory sharedMemory;

    private final int receiverPID;

    public LoggerSender(Mutex sharedMemoryMutex, int receiverPID) {
        sharedMemory = SharedMemory.open("/logmem", sharedMemoryMutex);
        this.receiverPID = receiverPID;
    }

    public void log(String message) {
        try {
            String data = new Date() + "\t" + Runtime.getCurrentProcess().getPID() + "\t" + message + "\n";
            sharedMemory.getOutputStream().write(data.getBytes());
            Runtime.kill(receiverPID, Signal.SIGUSR1);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
