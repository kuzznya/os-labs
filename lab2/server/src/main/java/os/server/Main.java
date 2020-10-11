package os.server;

import os.process.Process;
import os.process.Runtime;
import os.process.Signal;
import os.utils.Loader;

import java.util.LinkedHashMap;

public class Main {

    public static void main(String[] args) {
        Loader.loadNativeLibrary();

        Signal.addHook(Signal.SIGTERM, () -> {
            System.out.println(Runtime.getForkStatus() + " SIGTERM");
            Runtime.getChildren().forEach(Process::kill);
            Runtime.exit();
        });
        Signal.addHook(Signal.SIGSTOP, () -> {
            System.out.println(Runtime.getForkStatus() + " SIGSTOP");
            Runtime.exit();
        });

        Server server = new Server(8080);
        server.registerMapping("/test", request ->
                new Response(ResponseStatus.OK, "/test(/*)?", new LinkedHashMap<>(), "HELLO"));
        server.setDefaultMapping(request ->
                new Response(ResponseStatus.IM_A_TEAPOT, "/", new LinkedHashMap<>(), "I'm a teapot"));
        server.run();
    }
}
