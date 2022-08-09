package com.jeorgius.jbackend.validation;

import java.util.Optional;

@FunctionalInterface
public interface IValidator {
    Optional<ValidationError> validate();
}
