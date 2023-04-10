package xeonu.bankingserver.common.exception;

import lombok.Getter;
import xeonu.bankingserver.common.exception.handler.ErrorResponse;

@Getter
public class BadRequestException extends RuntimeException {

  private ErrorResponse errorResponse;

  public BadRequestException(ErrorResponse errorResponse) {
    this.errorResponse = ErrorResponse.
        of(errorResponse.getHttpStatus(), errorResponse.getMessage());
  }
}
