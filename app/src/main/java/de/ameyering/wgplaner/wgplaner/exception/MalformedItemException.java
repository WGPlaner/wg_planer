package de.ameyering.wgplaner.wgplaner.exception;

/**
 * Created by D067867 on 21.10.2017.
 */

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
