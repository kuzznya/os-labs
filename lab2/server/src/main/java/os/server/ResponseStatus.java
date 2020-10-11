package os.server;

public class ResponseStatus {

    private final int code;
    private final String description;

    public ResponseStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return code + " " + description;
    }

    public static final ResponseStatus OK = new ResponseStatus(200, "OK");
    public static final ResponseStatus BAD_REQUEST = new ResponseStatus(400, "Bad Request");
    public static final ResponseStatus UNAUTHORIZED = new ResponseStatus(401, "Unauthorized");
    public static final ResponseStatus FORBIDDEN = new ResponseStatus(403, "Forbidden");
    public static final ResponseStatus NOT_FOUND = new ResponseStatus(404, "Not Found");
    public static final ResponseStatus IM_A_TEAPOT = new ResponseStatus(418, "I'm a teapot");
    public static final ResponseStatus INTERNAL_ERROR = new ResponseStatus(500, "Internal Server Error");
}
