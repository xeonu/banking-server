package xeonu.bankingserver.common.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import xeonu.bankingserver.common.exception.BadRequestException;

@RestController
@ControllerAdvice
public class GlobalExceptionHandlers {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handlerMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST,
        e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorResponse> handlerBadRequestException(BadRequestException e) {
    ErrorResponse errorResponse = e.getErrorResponse();
    HttpStatus httpStatus = errorResponse.getHttpStatus();
    return new ResponseEntity<>(errorResponse, httpStatus);
  }
}
