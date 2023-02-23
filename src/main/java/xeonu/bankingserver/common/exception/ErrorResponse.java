package xeonu.bankingserver.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ErrorResponse {

  private HttpStatus httpStatus;
  private String message;
}
