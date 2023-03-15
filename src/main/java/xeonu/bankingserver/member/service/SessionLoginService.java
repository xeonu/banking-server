package xeonu.bankingserver.member.service;

import static xeonu.bankingserver.common.Utility.SessionKeys.LOGIN_KEY;
import static xeonu.bankingserver.member.exception.MemberErrorResponse.NOT_LOGIN;

import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import xeonu.bankingserver.common.exception.BadRequestException;

@Service
@RequiredArgsConstructor
@Primary
public class SessionLoginService implements LoginService {

  private final HttpSession httpSession;

  @Override
  public void login(int id) {
    httpSession.setAttribute(LOGIN_KEY, id);
    httpSession.setMaxInactiveInterval(40 * 60);
  }

  @Override
  public void logout() {
    httpSession.invalidate();
  }

  @Override
  public Integer getLoginMemberId() {
    Integer memberId = (Integer) httpSession.getAttribute(LOGIN_KEY);

    if (memberId == null) {
      throw new BadRequestException(NOT_LOGIN.getErrorResponse());
    }

    return memberId;
  }
}
