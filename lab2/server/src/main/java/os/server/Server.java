package os.server;

import os.process.Runtime;
import os.socket.ServerSocket;
import os.socket.Socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

public class Server {

    private final ServerSocket server;

    private Map<String, Function<Request, Response>> mappings = new LinkedHashMap<>();

    Function<Request, Response> defaultMapping = null;

    public Server(int port) {
        server = new ServerSocket((short) port);
        server.listen();
    }

    public void registerMapping(String path, Function<Request, Response> mapping) {
        this.mappings.put(path, mapping);
    }

    public void setDefaultMapping(Function<Request, Response> mapping) {
        this.defaultMapping = mapping;
    }

    private void handleError(Socket client, Exception ex, ResponseStatus status) {
        if (ex != null)
            ex.printStackTrace();
        try {
            client.getOutputStream()
                    .write(
                            new Response(
                                    status,
                                    "/",
                                    new LinkedHashMap<>(),
                                    ex != null ? ex.getMessage() : ""
                            ).toString().getBytes()
                    );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRequest(Socket client) {
        try (client) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            StringBuilder requestBuilder = new StringBuilder();

            while (true) {
                String line = reader.readLine();
                if (line == null)
                    break;
                requestBuilder.append(line).append('\n');
            }

            Request request = Request.parse(requestBuilder.toString());

            Response response = null;
            Optional<String> mappingKey = mappings
                    .keySet()
                    .stream()
                    .filter(key -> Pattern
                            .compile(key)
                            .matcher(request.getPath())
                            .find()
                    )
                    .findFirst();

            if (mappingKey.isPresent())
                response = mappings.get(mappingKey.get()).apply(request);
            else if (defaultMapping != null)
                response = defaultMapping.apply(request);

            if (response != null)
                client.getOutputStream().write(response.toString().getBytes());
            else
                handleError(client, null, ResponseStatus.NOT_FOUND);

        } catch (Exception ex) {
            handleError(client, ex, ResponseStatus.INTERNAL_ERROR);
        }
    }

    public void run() {
        try (server) {
            while (true) {
                try (Socket client = server.accept()) {
                    Runtime.childRun(() -> {
                        handleRequest(client);
                        client.close();
                        server.close();
                        Runtime.exit();
                    });
                }
            }
        }
    }
}
