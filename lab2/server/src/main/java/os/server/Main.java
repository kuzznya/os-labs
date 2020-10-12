package os.server;

import os.process.Runtime;
import os.process.Signal;
import os.utils.Loader;

import java.util.LinkedHashMap;

public class Main {

    public static void main(String[] args) {
        Loader.loadNativeLibrary();

        Signal.addHook(Signal.SIGTERM, Runtime::commitSuicide);
        Signal.addHook(Signal.SIGSTOP, Runtime::commitSuicide);

        Runtime.childRun(() -> {
            Server server = new Server(8080);
            server.registerMapping("/test(/*)?", request ->
                    new Response(ResponseStatus.OK, "/test", new LinkedHashMap<>(), "HELLO"));
            server.registerMapping("/error$", request -> {
                throw new RuntimeException("ERROR");
            });
            server.setDefaultMapping(request ->
                    new Response(ResponseStatus.IM_A_TEAPOT, "/", new LinkedHashMap<>(), "I'm a teapot"));
            server.run();
        });
        System.out.println(Runtime.getChildren().get(0).getPID() + " is daemon");
        System.out.println("I'm leaving you with the daemon");
        Runtime.commitSuicide();
    }
}
