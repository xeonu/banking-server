package xeonu.bankingserver.member.service;

public interface LoginService {

  /**
   * 로그인을 수행합니다.
   *
   * @param id 로그인하려는 member의 id
   */
  public void login(int id);

  /**
   * 로그아웃을 수행합니다.
   */
  public void logout();
}
