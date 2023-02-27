package xeonu.bankingserver.member.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorResponse {

  // 404 NOT_FOUND
  MEMBER_NOT_EXIST(NOT_FOUND, "회원이 존재하지 않습니다."),

  // 401 BAD_REQUEST
  INCORRECT_LOGIN_INFO(BAD_REQUEST, "로그인 정보가 일치하지 않습니다."),

  // 409 CONFLICT
  DUPLICATED_LOGIN_ID(CONFLICT, "이미 존재하는 아이디입니다.");

  private HttpStatus httpStatus;
  private String message;
}
