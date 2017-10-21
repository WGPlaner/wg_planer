package de.ameyering.wgplaner.wgplaner.exception;

public class MalformedItemException extends Exception {
    private String message = null;
    private Throwable cause = null;

    public MalformedItemException() {
        super();
    }

    public MalformedItemException(String message) {
        super(message);
        this.message = message;
    }

    public MalformedItemException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.cause = cause;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public synchronized Throwable getCause() {
        return cause;
    }
}
