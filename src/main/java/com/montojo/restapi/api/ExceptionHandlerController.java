package com.montojo.restapi.api;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ExceptionHandlerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerController.class);

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        LOGGER.warn("\n\nException logged by {}: {}\n\n", Thread.currentThread().getStackTrace()[1].getMethodName(), ex.toString());
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    public Map<String, String> handleMediaTypeExceptions(HttpMediaTypeException ex) {
        LOGGER.warn("\n\nException logged by {}: {}\n\n", Thread.currentThread().getStackTrace()[1].getMethodName(), ex.toString());
        Map<String, String> errors = new LinkedHashMap<>();
        errors.put("Message", ex.getBody().getDetail());
        errors.put("Detail", "Payload as JSON required.");
        return errors;
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public Map<String, String> handleJSONParsingExceptions(HttpMessageNotReadableException ex) {
        LOGGER.warn("\n\nException logged by {}: {}\n\n", Thread.currentThread().getStackTrace()[1].getMethodName(), ex.toString());
        Map<String, String> errors = new LinkedHashMap<>();
        errors.put("Message", String.format("%s", ex.getMessage()));
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public Map<String, String> handleMethodArgumentExceptions(MethodArgumentTypeMismatchException ex) {
        LOGGER.warn("\n\nException logged by {}: {}\n\n", Thread.currentThread().getStackTrace()[1].getMethodName(), ex.toString());
        Map<String, String> errors = new LinkedHashMap<>();
        errors.put("Message", "Parameter type mismatch");
        errors.put("Required type", ex.getRequiredType().getSimpleName());
        errors.put("Provided type", ex.getValue().getClass().getSimpleName());
        errors.put("Parameter value", ex.getValue().toString());
        return errors;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NoSuchElementException.class})
    public Map<String, String> handleMethodNoSuchElementExceptions(NoSuchElementException ex) {
        LOGGER.warn("\n\nException logged by {}: {}\n\n", Thread.currentThread().getStackTrace()[1].getMethodName(), ex.toString());
        Map<String, String> errors = new LinkedHashMap<>();
        errors.put("Message", String.format("No results for query: %s.", ex.getMessage()));
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class})
    public Map<String, String> handleMethodNoSuchElementExceptions(IllegalArgumentException ex) {
        LOGGER.warn("\n\nException logged by {}: {}\n\n", Thread.currentThread().getStackTrace()[1].getMethodName(), ex.toString());
        Map<String, String> errors = new LinkedHashMap<>();
        errors.put("Message", ex.getMessage());
        return errors;
    }
}
