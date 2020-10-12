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

        Server server = new Server(8080);
        server.registerMapping("/test", request ->
                new Response(ResponseStatus.OK, "/test(/*)?", new LinkedHashMap<>(), "HELLO"));
        server.setDefaultMapping(request ->
                new Response(ResponseStatus.IM_A_TEAPOT, "/", new LinkedHashMap<>(), "I'm a teapot"));
        server.run();
    }
}
