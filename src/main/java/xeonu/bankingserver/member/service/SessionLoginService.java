package xeonu.bankingserver.member.service;

import static xeonu.bankingserver.common.Utility.SessionKeys.LOGIN_KEY;

import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

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
}
