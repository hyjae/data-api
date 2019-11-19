package kr.datasolution.ds.api.advice;

import com.google.gson.Gson;
import kr.datasolution.ds.api.controller.NewsController;
import kr.datasolution.ds.api.domain.ApiError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Collection;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice(assignableTypes = {NewsController.class})
public class ApiControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<?> invalidParamsExceptionHandler(ConstraintViolationException e) {
        ApiError apiError = new ApiError(NOT_FOUND);
        String message = resolveConstraintViolations(e.getConstraintViolations());
        apiError.setMessage(message);
        return buildResponseEntity(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = "Malformed JSON request";
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
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
