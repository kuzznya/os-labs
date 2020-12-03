package os.server;

import os.logger.LoggerReceiver;
import os.logger.LoggerSender;
import os.process.Runtime;
import os.process.Signal;
import os.utils.Loader;

import java.util.LinkedHashMap;

public class Main {

    public static void main(String[] args) {
        Loader.loadNativeLibrary();

        Signal.addHook(Signal.SIGTERM, Runtime::commitSuicide);
        Signal.addHook(Signal.SIGSTOP, Runtime::commitSuicide);

        LoggerReceiver receiver = new LoggerReceiver();
        receiver.start();
        Runtime.getLastChild().ifPresent(process -> System.out.println(process.getPID() + " is logger daemon"));
        LoggerSender sender = receiver.getSender();

        Runtime.kill(receiver.getReceiverPID(), Signal.SIGUSR1);

        Runtime.childRun(() -> {
            Server server = new Server(8080);
            server.registerMapping("/test(/*)?", request ->
                    new Response(ResponseStatus.OK, new LinkedHashMap<>(), "HELLO"));
            server.registerMapping("/error$", request -> {
                sender.log("ERROR BLYAT");
                throw new RuntimeException("ERROR");
            });
            server.setDefaultMapping(request ->
                    new Response(ResponseStatus.IM_A_TEAPOT, new LinkedHashMap<>(), "I'm a teapot"));
            server.run();
        });
        System.out.println(Runtime.getLastChild().orElseThrow().getPID() + " is server daemon");
        System.out.println("I'm leaving you with the daemons");
        Runtime.commitSuicide();
    }
}
