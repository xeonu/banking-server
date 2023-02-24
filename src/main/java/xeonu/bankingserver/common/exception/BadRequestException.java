package xeonu.bankingserver.common.exception;

import lombok.Getter;
import xeonu.bankingserver.common.exception.handler.ErrorResponse;
import xeonu.bankingserver.member.exception.MemberErrorResponse;

@Getter
public class BadRequestException extends RuntimeException {

  private ErrorResponse errorResponse;

  public BadRequestException(MemberErrorResponse memberErrorResponse) {
    this.errorResponse = ErrorResponse.
        of(memberErrorResponse.getHttpStatus(), memberErrorResponse.getMessage());
  }
}
