package os.server;

import os.socket.InetSocketAddress;
import os.socket.ServerSocket;
import os.socket.Socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        try (ServerSocket socket = new ServerSocket()) {
            socket.create();
            socket.bind(new InetSocketAddress((short) 8080));
            socket.listen();
            while (true) {
                try (Socket client = socket.accept()) {
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
