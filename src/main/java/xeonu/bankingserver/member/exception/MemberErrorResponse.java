package xeonu.bankingserver.member.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static xeonu.bankingserver.common.exception.handler.ErrorResponse.of;

import lombok.AllArgsConstructor;
import lombok.Getter;
import xeonu.bankingserver.common.exception.handler.ErrorResponse;

@Getter
@AllArgsConstructor
public enum MemberErrorResponse {

  // 404 NOT_FOUND
  MEMBER_NOT_EXIST(of(NOT_FOUND, "회원이 존재하지 않습니다.")),
  NOT_LOGIN(of(NOT_FOUND, "로그인이 되어있지 않습니다.")),

  // 401 BAD_REQUEST
  INCORRECT_LOGIN_INFO(of(BAD_REQUEST, "로그인 정보가 일치하지 않습니다.")),

  // 409 CONFLICT
  DUPLICATED_LOGIN_ID(of(CONFLICT, "이미 존재하는 아이디입니다."));

  private ErrorResponse errorResponse;
}
