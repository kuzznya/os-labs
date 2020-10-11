package os.server;

import java.util.Date;
import java.util.Map;

public class Response {
    private final ResponseStatus status;
    private final String path;
    private final Map<String, String> headers;
    private final String body;

    public Response(ResponseStatus status, String path, Map<String, String> headers, String body) {
        this.status = status;
        this.path = path;
        this.headers = headers;
        this.body = body;
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
