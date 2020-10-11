package os.server;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class Request {
    private RequestMethod method;
    private String path;
    private Map<String, String> headers;
    private String body;

    public Request(RequestMethod method, String path) {
        this.method = method;
        this.path = path;
        this.headers = new LinkedHashMap<>();
    }

    public Request(RequestMethod method, String path, Map<String, String> headers) {
        this.method = method;
        this.path = path;
        this.headers = headers;
    }

    public Request(RequestMethod method, String path, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Optional<String> getBody() {
        return Optional.ofNullable(body);
    }

    public static Request parse(String request) {
        Scanner scanner = new Scanner(request);

        RequestMethod method = RequestMethod.valueOf(scanner.next());
        String path = scanner.next();

        String version = scanner.next();
        if (!version.equals("HTTP/1.1"))
            throw new IllegalArgumentException(version + " is illegal HTTP version");

        Map<String, String> headers = new LinkedHashMap<>();

        String line = scanner.nextLine();
        while (!line.isEmpty()) {
            String headerKey = line.split(":", 2)[0];
            String headerValue = line.split(":", 2)[1];
            headers.put(headerKey, headerValue);
            line = scanner.nextLine();
        }

        StringBuilder bodyBuilder = new StringBuilder();
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if (!line.isEmpty())
                bodyBuilder.append(line);
        }

        if (bodyBuilder.toString().isEmpty())
            return new Request(method, path, headers);
        else
            return new Request(method, path, headers, bodyBuilder.toString());
    }
}
