package xeonu.bankingserver.member.service;

public interface EncryptService {

  /**
   * 문자열을 암호화합니다.
   * @param plain 평문
   * @return 비문
   */
  public String encrypt(String plain);

  /**
   * 해당 비문이 평문을 암호화 한 것인지 확인합니다.
   * @param plain 평문
   * @param hashpw 비문
   * @return 비문이 평문을 암호환 한 것인지 여부
   */
  public boolean checkPassword(String plain, String hashpw);
}
