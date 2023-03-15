package xeonu.bankingserver.common.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ErrorResponse {

  private HttpStatus httpStatus;
  private String message;

  public static ErrorResponse of(HttpStatus httpStatus, String message) {
    return new ErrorResponse(httpStatus, message);
  }
}
