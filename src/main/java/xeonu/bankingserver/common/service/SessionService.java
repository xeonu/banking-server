package xeonu.bankingserver.common.service;

import static xeonu.bankingserver.common.exception.CommonErrorReponse.SESSION_NOT_EXIST;

import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xeonu.bankingserver.common.exception.BadRequestException;

@Service
@RequiredArgsConstructor
public class SessionService {

  private final HttpSession httpSession;

  public String getValue(String key) {
    String value = (String) httpSession.getAttribute(key);
    if (value == null) {
      throw new BadRequestException(SESSION_NOT_EXIST.getErrorResponse());
    }

    return value;
  }

}
