package app.hisend.exceptions;

public class HisendException extends RuntimeException {
    private final Integer statusCode;

    public HisendException(String message) {
        super(message);
        this.statusCode = null;
    }

    public HisendException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = null;
    }

    public HisendException(String message, Integer statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
