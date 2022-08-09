package com.jeorgius.jbackend.validation;

import com.google.common.collect.Sets;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import javax.validation.ConstraintViolation;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Negative;
import javax.validation.constraints.NegativeOrZero;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Past;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;

public class ValidationError {
    private Map<String, Set<String>> fields = new LinkedHashMap<>();
    private Set<String> messages = new LinkedHashSet<>();

    public ValidationError(String message) {
        messages.add(message);
    }

    public ValidationError(Collection<String> list) {
        messages.addAll(list);
    }

    public ValidationError() {
    }

    public ValidationError(List<ObjectError> errors) {
        errors.forEach(err -> put(((FieldError) err).getField(), err.getCode()));
    }

    public ValidationError(String key, String value) {
        put(key, value);
    }

    public static <T> Optional<ValidationError> ofConstraints(Set<ConstraintViolation<T>> set) {
        if (!set.isEmpty()) {
            var ret = new ValidationError();
            for (ConstraintViolation violation : set) {
                ret.put(violation.getPropertyPath().toString(), getDescriptionForValidationError(violation));
            }
            return Optional.of(ret);
        }

        return Optional.empty();
    }

    private static String getDescriptionForValidationError(ConstraintViolation violation) {
        if (violation.getConstraintDescriptor() == null) {
            return "Неизвестная ошибка";
        }
        if (violation.getConstraintDescriptor().getAnnotation() instanceof AssertFalse) {
            return "Значение должно быть ложным";
        }
        if (violation.getConstraintDescriptor().getAnnotation() instanceof AssertTrue) {
            return "Значение должно быть ложным";
        }
        if (violation.getConstraintDescriptor().getAnnotation() instanceof DecimalMax) {
            return "Значение должно быть меньше или равно установленному максимуму";
        }
        if (violation.getConstraintDescriptor().getAnnotation() instanceof DecimalMin) {
            return "Значение должно быть больше или равно установленному минимуму";
        }
        if (violation.getConstraintDescriptor().getAnnotation() instanceof Digits) {
            return " Значение должно быть в пределах допустимого диапазона";
        }
        if (violation.getConstraintDescriptor().getAnnotation() instanceof Email) {
            return "Адрес электронной почты введен некорректно";
        }
        if (violation.getConstraintDescriptor().getAnnotation() instanceof Future) {
            return "Дата должна быть указана в будущем времени";
        }
        if (violation.getConstraintDescriptor().getAnnotation() instanceof FutureOrPresent) {
            return "Дата должна быть указана в настоящем или будущем времени";
        }
        if (violation.getConstraintDescriptor().getAnnotation() instanceof Max) {
            return "Значение должно быть меньше или равно установленному максимуму";
        }
        if (violation.getConstraintDescriptor().getAnnotation() instanceof Min) {
            return "Значение должно быть больше или равно установленному минимуму";
        }
        if (violation.getConstraintDescriptor().getAnnotation() instanceof Negative) {
            return "Значение должно быть строго отрицательным числом";
        }
        if (violation.getConstraintDescriptor().getAnnotation() instanceof NegativeOrZero) {
            return "Значение должно быть неположительным числом";
        }
        if (violation.getConstraintDescriptor().getAnnotation() instanceof NotBlank) {
            return "Значение должно содержать хотя бы один символ";
        }
        if (violation.getConstraintDescriptor().getAnnotation() instanceof NotEmpty) {
            return "Поле должно быть заполнено";
        }
        if (violation.getConstraintDescriptor().getAnnotation() instanceof NotNull) {
            return "Поле должно быть заполнено";
        }
        if (violation.getConstraintDescriptor().getAnnotation() instanceof Null) {
            return "Значение не должно быть заполнено";
        }
        if (violation.getConstraintDescriptor().getAnnotation() instanceof Past) {
            return "Дата дожна быть указана в прошлом времени";
        }
        if (violation.getConstraintDescriptor().getAnnotation() instanceof PastOrPresent) {
            return "Дата дожна быть указана не позднее чем в текущий момент времени";
        }
        if (violation.getConstraintDescriptor().getAnnotation() instanceof Pattern) {
            return "Значение должно соответствовать указанному шаблону";
        }
        if (violation.getConstraintDescriptor().getAnnotation() instanceof Positive) {
            return "Число должно быть больше нуля";
        }
        if (violation.getConstraintDescriptor().getAnnotation() instanceof PositiveOrZero) {
            return "Значение должно быть неотрицательным числом";
        }
        if (violation.getConstraintDescriptor().getAnnotation() instanceof Size) {
            return "Размер значение должен быть в указанных границах";
        }
        return violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();
    }

    public Set<String> getMessages() {
        return messages;
    }

    public Map<String, Set<String>> getFields() {
        return fields;
    }

    public void put(String key, String value) {
        if (fields.containsKey(key)) {
            fields.get(key).add(value);
        } else {
            fields.put(key, Sets.newHashSet(value));
        }
    }

    public void put(String key, Set<String> value) {
        value.forEach(row -> put(key, row));
    }

    public void message(String message) {
        messages.add(message);
    }

    public void putAll(ValidationError other) {
        if (other == null) {
            return;
        }
        other.fields.forEach(this::put);
        other.messages.forEach(this::message);
    }

    @Override
    public String toString() {
        return "ValidationError{" +
                "fields=" + fields +
                "messages=" + messages +
                '}';
    }
}
