package com.jeorgius.jbackend.validation;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;

@Component
@NoArgsConstructor
@RequiredArgsConstructor
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class ValidationBuilder {

    @NonNull
    @Autowired
    private Validator validator;

    private com.jeorgius.jbackend.validation.ValidationError errors = new ValidationError();
    private List<IValidator> validators = new ArrayList<>();

    public void validate() {
        try {
            for (IValidator validator : validators) {
                validator.validate().ifPresent(errors::putAll);
            }
            if (!errors.getFields().isEmpty() || !errors.getMessages().isEmpty()) {
                throw new ValidationException(errors);
            }
        } finally {
            errors = new ValidationError();
            validators.clear();
        }
    }

    public boolean hasErrors() {
        return errors.getMessages().size() > 0;
    }

    public ValidationBuilder dto(Object dto) {
        validators.add(new DtoValidator(dto));
        return this;
    }

    public ValidationBuilder custom(CustomValidator validator) {
        validators.add(validator);
        return this;
    }

    public ValidationBuilder other(IValidator validator) {
        validators.add(validator);
        return this;
    }

    public ValidationBuilder other(BooleanSupplier condition, String... messages) {
        return other(condition, null, null, messages);
    }

    public ValidationBuilder other(BooleanSupplier condition, String errorKey, String errorVal) {
        return other(condition, errorKey, errorVal, null);
    }

    public ValidationBuilder other(BooleanSupplier condition, String errorKey, String errorVal, String... messages) {
        validators.add(new ConditionValidator(condition, errorKey, errorVal, messages));
        return this;
    }

    public ValidationBuilder other(boolean condition, String errorKey, String errorMessage) {
        validators.add(() -> {
            if (condition) {
                return Optional.of(new ValidationError(errorKey, errorMessage));
            }
            return Optional.empty();
        });
        return this;
    }

    public ValidationBuilder other(boolean condition, String errorMessage) {
        validators.add(() -> {
            if (condition) {
                return Optional.of(new ValidationError(errorMessage));
            }
            return Optional.empty();
        });
        return this;
    }

    private class ConditionValidator implements IValidator {
        private BooleanSupplier condition;
        private String errorKey;
        private String errorVal;
        private String[] messages;

        public ConditionValidator(BooleanSupplier condition, String errorKey, String errorVal, String[] messages) {
            this.condition = condition;
            this.errorKey = errorKey;
            this.errorVal = errorVal;
            this.messages = messages;
        }

        @Override
        public Optional<ValidationError> validate() {
            if (condition.getAsBoolean()) {
                ValidationError result = new ValidationError();
                if (errorKey != null) {
                    result.put(errorKey, errorVal);
                }
                if (messages != null) {
                    for (String message : messages) {
                        result.message(message);
                    }
                }
                return Optional.of(result);
            }
            return Optional.empty();
        }
    }

    private class DtoValidator implements IValidator {
        private Object dto;

        public DtoValidator(Object dto) {
            this.dto = dto;
        }

        @Override
        public Optional<ValidationError> validate() {
            return ValidationError.ofConstraints(validator.validate(dto));
        }
    }
}
