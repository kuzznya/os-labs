package os.server;

import os.socket.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        try (ServerSocket socket = new ServerSocket()) {
            socket.create();
            socket.bind(new InetSocketAddress((short) 8080));
            while (true) {
                socket.listen();
                try (Socket client = socket.accept()) {
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
//                    StringBuilder lines = new StringBuilder();
//
//                    String line = reader.readLine();
//                    while (line != null && !line.isEmpty()) {
//                        line = reader.readLine();
//                        if (line == null || line.isEmpty())
//                            break;
//                        lines.append(line).append("\r\n");
//                    }
                    byte[] buffer = new byte[1024];
                    int len = client.getInputStream().read(buffer);
                    String lines = new String(buffer, 0, len);

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
