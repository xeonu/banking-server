package xeonu.bankingserver.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
   * @param signUpDto 아이디와 비밀번호가 포함된 정보
   */
  public void signUp(SignUpDto signUpDto) {
    Member newMember = Member.builder()
        .loginId(signUpDto.getLoginId())
        .password(encryptService.encrypt(signUpDto.getPassword()))
        .build();

    repository.save(newMember);
  }

}
