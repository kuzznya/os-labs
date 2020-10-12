package os.server;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Response {
    private final ResponseStatus status;
    private final Map<String, String> headers;
    private final String body;

    public Response(ResponseStatus status, Map<String, String> headers, String body) {
        this.status = status;
        this.headers = headers;
        this.body = body;

        addDefaultHeaders();
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    private void addDefaultHeaders() {
        if (!headers.containsKey("Connection"))
            headers.put("Connection", "keep-alive");
        if (!headers.containsKey("Content-Type"))
            headers.put("Content-Type", "text/plain; charset=utf-8");
        if (!headers.containsKey("Date"))
            headers.put("Date", new Date().toString());
        if (!headers.containsKey("Content-Length"))
            headers.put("Content-Length", String.valueOf(body.length()));
    }

    public static Response parse(String response) {
        Scanner scanner = new Scanner(response);

        String version = scanner.next();
        if (!version.equals("HTTP/1.1"))
            throw new IllegalArgumentException(version + " is illegal HTTP version");

        ResponseStatus status = new ResponseStatus(scanner.nextInt(), scanner.nextLine().trim());

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

        return new Response(status, headers, bodyBuilder.toString());
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("HTTP/1.1 ")
                .append(status.toString())
                .append("\r\n");

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

        stringBuilder
                .append(headersString)
                .append("\r\n")
                .append(body);

        return stringBuilder.toString();
    }
}
