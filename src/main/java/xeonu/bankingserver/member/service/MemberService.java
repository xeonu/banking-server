package xeonu.bankingserver.member.service;

import static xeonu.bankingserver.member.exception.MemberErrorResponse.DUPLICATED_LOGIN_ID;
import static xeonu.bankingserver.member.exception.MemberErrorResponse.MEMBER_NOT_EXIST;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xeonu.bankingserver.common.exception.BadRequestException;
import xeonu.bankingserver.member.dto.SignUpDto;
import xeonu.bankingserver.member.entity.Member;
import xeonu.bankingserver.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository repository;
  private final EncryptService encryptService;

  /**
   * 회원가입을 수행합니다.
   *
   * @param signUpDto 아이디와 비밀번호가 포함된 정보
   */
  public void signUp(SignUpDto signUpDto) {
    String loginId = signUpDto.getLoginId();

    if (existsByLoginId(loginId)) {
      throw new BadRequestException(DUPLICATED_LOGIN_ID);
    }

    String password = encryptService.encrypt(signUpDto.getPassword());
    Member newMember = Member.builder().
        loginId(loginId).
        password(password).
        build();

    repository.save(newMember);
  }

  /**
   * 로그인 아이디가 이미 존재하는지 확인하고 존재한다면 BadRequestException을 던집니다.
   *
   * @param loginId 회원의 loginId
   */
  public void loginIdDuplicateCheck(String loginId) {
    boolean isDuplicated = existsByLoginId(loginId);

    if (isDuplicated) {
      throw new BadRequestException(DUPLICATED_LOGIN_ID);
    }
  }

  /**
   * 회원의 로그인 아이디가 존재하는지 확인합니다.
   *
   * @param loginId 회원의 loginId
   * @return 로그인아이디 존재 유무
   */
  public boolean existsByLoginId(String loginId) {
    return repository.existsMemberByLoginId(loginId);
  }

  /**
   * 특정 id의 회원정보를 조회합니다.
   *
   * @param id 회원의 id
   * @return 특정 id의 회원정보
   */
  public Member getMemberById(int id) {
    Optional<Member> member = repository.findById(id);

    if (member.isEmpty()) {
      throw new BadRequestException(MEMBER_NOT_EXIST);
    }

    return member.get();
  }
}
