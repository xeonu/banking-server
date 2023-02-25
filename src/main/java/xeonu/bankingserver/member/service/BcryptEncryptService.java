package xeonu.bankingserver.member.service;

import static org.mindrot.jbcrypt.BCrypt.hashpw;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class BcryptEncryptService implements EncryptService {

  @Override
  public String encrypt(String plain) {
    String hashpw = hashpw(plain, BCrypt.gensalt());
    return hashpw;
  }

  @Override
  public boolean checkPassword(String plain, String hashpw) {
    return BCrypt.checkpw(plain, hashpw);
  }
}
