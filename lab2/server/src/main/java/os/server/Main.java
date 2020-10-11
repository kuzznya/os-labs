package os.server;

import os.process.Process;
import os.process.Runtime;
import os.process.Signal;
import os.socket.ServerSocket;
import os.socket.Socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
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
        try (ServerSocket server = new ServerSocket((short) 8080)) {
            Signal.addHook(Signal.SIGTERM, () -> {
                System.out.println(Runtime.getForkStatus() + " SIGTERM");
                Runtime.getChildren().forEach(Process::kill);
//                server.close();
                Runtime.exit();
            });
            Signal.addHook(Signal.SIGSTOP, () -> {
                System.out.println(Runtime.getForkStatus() + " SIGSTOP");
                System.exit(0);
            });

            server.listen();
            while (true) {
                try (Socket client = server.accept()) {
                    Runtime.childRun(() -> {
                        echoClientRequest(client);
                        System.out.println(Runtime.getForkStatus() +  ": Exiting now");
                        client.close();
                        server.close();
                        Runtime.exit();
                    });
                    System.out.println(Runtime.getForkStatus().name() + ": I'm alive");
                }
            }
        }
    }
}
