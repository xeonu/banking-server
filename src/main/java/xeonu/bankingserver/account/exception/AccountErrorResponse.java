package xeonu.bankingserver.account.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static xeonu.bankingserver.common.exception.handler.ErrorResponse.of;

import lombok.AllArgsConstructor;
import lombok.Getter;
import xeonu.bankingserver.common.exception.handler.ErrorResponse;

@Getter
@AllArgsConstructor
public enum AccountErrorResponse {

  // 400 BAD_REQUEST
  NOT_ACCOUNT_OWNER(of(BAD_REQUEST, "해당 계좌의 계좌주가 아닙니다.")),

  // 404 NOT_FOUND
  ACCOUNT_NOT_EXIST(of(NOT_FOUND, "존재하지 않은 계좌입니다.")),

  // 409 CONFLICT
  DUPLICATED_ACCOUNT_NUMBER(of(CONFLICT, "이미 존재하는 계좌번호입니다."));

  private ErrorResponse errorResponse;
}
