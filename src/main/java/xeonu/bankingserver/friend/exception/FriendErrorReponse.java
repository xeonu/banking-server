package xeonu.bankingserver.friend.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static xeonu.bankingserver.common.exception.handler.ErrorResponse.of;

import lombok.AllArgsConstructor;
import lombok.Getter;
import xeonu.bankingserver.common.exception.handler.ErrorResponse;

@Getter
@AllArgsConstructor
public enum FriendErrorReponse {

  // 404 NOT_FOUND
  FRIEND_REQUEST_NOT_EXIST(of(NOT_FOUND, "친구신청 내역이 존재하지 않습니다.")),

  // 404 BAD_REQUEST
  FRIEND_REQUEST_NOT_MATCH(of(BAD_REQUEST, "잘못된 친구신청 정보입니다.")),

  // 409 CONFLICT
  FRIEND_REQUEST_ALREADY_SENT(of(CONFLICT, "이미 친구신청을 보냈습니다.")),
  FRIEND_ALREADY(of(CONFLICT, "이미 친구입니다."));

  private ErrorResponse errorResponse;
}
