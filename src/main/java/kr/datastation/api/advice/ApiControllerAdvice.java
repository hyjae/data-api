package kr.datastation.api.advice;

import com.google.gson.Gson;
import kr.datastation.api.controller.NewsController;
import kr.datastation.api.controller.WeatherController;
import kr.datastation.api.vo.ApiError;
import kr.datastation.api.repository.dataset.WeatherDailyRepositoryImpl;
import kr.datastation.api.validator.DateHandlerResolver;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Collection;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice(assignableTypes = {NewsController.class, WeatherController.class, DateHandlerResolver.class, WeatherDailyRepositoryImpl.class})
public class ApiControllerAdvice extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiError apiError = new ApiError(NOT_FOUND);
        String message = ex.getMessage();
        apiError.setMessage(message);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<?> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        ApiError apiError = new ApiError(NOT_FOUND);
        String message = e.getMessage();
        apiError.setMessage(message);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler({IllegalStateException.class})
    public ResponseEntity<?> illegalStateExceptionHandler(IllegalStateException e) {
        ApiError apiError = new ApiError(NOT_FOUND);
        String message = e.getMessage();
        apiError.setMessage(message);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler({InvalidDataAccessApiUsageException.class})
    public ResponseEntity<?> invalidDataAccessApiUsageExceptionHandler(InvalidDataAccessApiUsageException e) {
        ApiError apiError = new ApiError(NOT_FOUND);
        String message = e.getMessage();
        apiError.setMessage(message);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<?> illegalArgumentExceptionHandler(IllegalArgumentException e) {
        ApiError apiError = new ApiError(NOT_FOUND);
        String message = e.getMessage();
        apiError.setMessage(message);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<?> invalidParamsExceptionHandler(ConstraintViolationException e) {
        ApiError apiError = new ApiError(NOT_FOUND);
        String message = resolveConstraintViolations(e.getConstraintViolations());
        apiError.setMessage(message);
        return buildResponseEntity(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = "Malformed JSON request";
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        ResponseEntity<String> stringResponseEntity = new ResponseEntity<>(new Gson().toJson(apiError), apiError.getStatus());
        return new ResponseEntity<>(new Gson().toJson(apiError), apiError.getStatus());
    }

    private String resolveConstraintViolations(Collection<ConstraintViolation<?>> constraintViolations) {
        return constraintViolations.stream().map(cv -> {
            String parameter = String.valueOf(cv.getPropertyPath());
            String validationMessage = cv.getMessage();
            return format("'%s': %s", parameter, validationMessage);
        }).collect(joining(", "));
    }
}
