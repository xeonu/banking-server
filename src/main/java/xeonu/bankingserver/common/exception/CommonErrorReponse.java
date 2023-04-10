package xeonu.bankingserver.common.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static xeonu.bankingserver.common.exception.handler.ErrorResponse.of;

import lombok.AllArgsConstructor;
import lombok.Getter;
import xeonu.bankingserver.common.exception.handler.ErrorResponse;

@Getter
@AllArgsConstructor
public enum CommonErrorReponse {

  // 404 NOT_FOUND
  SESSION_NOT_EXIST(of(NOT_FOUND, "세션이 존재하지 않습니다."));

  private ErrorResponse errorResponse;
}
