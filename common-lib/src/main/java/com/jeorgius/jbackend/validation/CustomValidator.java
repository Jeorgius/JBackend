package com.jeorgius.jbackend.validation;

public abstract class CustomValidator implements IValidator {
    private Object[] objects;

    public CustomValidator(Object... objects) {
        this.objects = objects;
    }

    public Object arg(int i) {
        return objects[i];
    }
}
