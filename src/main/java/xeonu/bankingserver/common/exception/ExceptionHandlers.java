package xeonu.bankingserver.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ControllerAdvice
public class ExceptionHandlers {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handlerMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST,
        e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
  }
}
