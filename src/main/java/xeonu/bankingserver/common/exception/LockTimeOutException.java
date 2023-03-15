package xeonu.bankingserver.common.exception;

import lombok.Getter;
import xeonu.bankingserver.common.exception.handler.ErrorResponse;

@Getter
public class LockTimeOutException extends RuntimeException {

  private ErrorResponse errorResponse;

  public LockTimeOutException(ErrorResponse errorResponse) {
    this.errorResponse = ErrorResponse.
        of(errorResponse.getHttpStatus(), errorResponse.getMessage());
  }
}
