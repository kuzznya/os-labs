package os.client;

import os.server.Request;
import os.server.RequestMethod;
import os.server.Response;
import os.socket.ClientSocket;
import os.socket.InetSocketAddress;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        try (ClientSocket socket = new ClientSocket()) {
            socket.connect(new InetSocketAddress((short) 8080, InetSocketAddress.INADDR_LOOPBACK));

            socket.getOutputStream()
                    .write(new Request(RequestMethod.GET, "/test",
                            Map.of("Accept", "*/*", "Connection", "keep-alive")
                            ).toString()
                            .getBytes(StandardCharsets.UTF_8)
                    );

            StringBuilder responseBuilder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (true) {
                String line = reader.readLine();
                if (line == null)
                    break;
                responseBuilder.append(line).append('\n');
            }

            Response response = Response.parse(responseBuilder.toString());

            System.out.println(response.getStatus());
            System.out.println(response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
