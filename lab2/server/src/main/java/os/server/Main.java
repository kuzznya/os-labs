package os.server;

import os.process.Process;
import os.process.Runtime;
import os.process.Signal;
import os.socket.Socket;
import os.utils.Loader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main {

    public static void echoClientRequest(Socket client) {
        try (client) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            StringBuilder lines = new StringBuilder();

            while (true) {
                String line = reader.readLine();
                if (line == null)
                    break;
                lines.append(line).append("\r\n");
            }

            Map<String, String> headers = Map.of(
                    "Connection", "keep-alive",
                    "Content-Type", "text/plain; charset=utf-8",
                    "Date", new Date().toString(),
                    "Content-Length", String.valueOf(lines.length())
            );

            String headersString = headers
                    .entrySet()
                    .stream()
                    .collect(StringBuffer::new,
                            (stringBuffer, entry) ->
                                    stringBuffer
                                            .append(entry.getKey())
                                            .append(": ")
                                            .append(entry.getValue())
                                            .append("\r\n"),
                            StringBuffer::append
                    ).toString();

            String response = "HTTP/1.1 200 OK\r\n" + headersString + "\r\n" + lines.toString();

            client.getOutputStream()
                    .write(response.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

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
