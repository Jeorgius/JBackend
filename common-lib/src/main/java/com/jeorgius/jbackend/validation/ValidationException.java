package com.jeorgius.jbackend.validation;


public class ValidationException extends RuntimeException {

    private ValidationError errors;

    public ValidationException(ValidationError errors) {
        super("Validation error", null, true, false);
        this.errors = errors;

    }

    public ValidationException(String value) {
        this(new ValidationError(value));
    }

    public ValidationError getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "ValidationException{" +
                "errors=" + errors +
                '}';
    }
}
