package ir.oliateaching.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;



public class DTOValidator {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    public static <T> Set<ConstraintViolation<T>> validate(T dto) {
        return validator.validate(dto);
    }

    public static <T> boolean isValid(T dto) {
        return validate(dto).isEmpty();
    }

    public static <T> String getValidationMessages(T dto) {
        Set<ConstraintViolation<T>> violations = validate(dto);

        if (violations.isEmpty()) {
            return "Data is valid.";
        }

        StringBuilder messages = new StringBuilder("Validation errors:\n");
        for (ConstraintViolation<T> violation : violations) {
            messages.append("  • ").append(violation.getMessage()).append("\n");
        }

        return messages.toString();
    }

    public static <T> void validateOrThrow(T dto) {
        Set<ConstraintViolation<T>> violations = validate(dto);

        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Validation failed:\n");
            for (ConstraintViolation<T> violation : violations) {
                errorMessage.append("- ").append(violation.getMessage()).append("\n");
            }
            throw new IllegalArgumentException(errorMessage.toString());
        }
    }
}
