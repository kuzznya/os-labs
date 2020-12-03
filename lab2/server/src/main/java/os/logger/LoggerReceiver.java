package os.logger;

import os.memory.SharedMemory;
import os.process.Process;
import os.process.Runtime;
import os.process.Signal;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class LoggerReceiver {

    private boolean started = false;

    private final SharedMemory sharedMemory = SharedMemory.create("/logmem");

    private int receiverPID;

    public int getReceiverPID() {
        return receiverPID;
    }

    public LoggerSender getSender() {
        if (!started)
            throw new RuntimeException("LoggerReceiver was not started yet");
        return new LoggerSender(sharedMemory.getMutex(), receiverPID);
    }

    private void logFromMemory() {
        try {
            System.out.println("LOG FROM MEMORY");

            StringBuilder dataBuilder = new StringBuilder();

            var reader = new BufferedReader(new InputStreamReader(sharedMemory.getInputStream()));

            while (true) {
                String line = reader.readLine();
                if (line == null)
                    break;
                dataBuilder.append(line);
            }

            // TODO: 14.10.2020 actual logging
            System.out.println(dataBuilder.toString());

        } catch (Exception ignored) { }
    }

    public void start() {
        if (started)
            return;

        started = true;

        Runtime.childRun(() -> {
            Runtime.addShutdownHook("CLOSE_SHARED", sharedMemory::close);
            receiverPID = Runtime.getCurrentProcess().getPID();
            Signal.addHook(Signal.SIGUSR1, this::logFromMemory);
            try (sharedMemory) {
                System.out.println("ENTERING WHILE LOOP");
                while (true) {
                    Runtime.pause();
                    System.out.println("LOGGER AWAKE");
                }
            }
        });

        receiverPID = Runtime.getLastChild().map(Process::getPID).orElse(0);
    }

    public void stop() {
        if (!started)
            return;

        Runtime.kill(receiverPID, Signal.SIGTERM);
        receiverPID = 0;
        started = false;
    }
}
