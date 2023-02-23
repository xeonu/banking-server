package xeonu.bankingserver.member.service;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import xeonu.bankingserver.member.dto.SignUpDto;
import xeonu.bankingserver.member.repository.MemberRepository;


@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

  @Mock
  MemberRepository memberRepository;

  @Test
  @DisplayName("회원가입")
  void SignUpTest() {
    BcryptEncryptService bcryptEncryptService = new BcryptEncryptService();
    MemberService memberService = new MemberService(memberRepository, bcryptEncryptService);

    SignUpDto signUpDto = SignUpDto.builder()
        .loginId("abcdabcd")
        .password("12345678")
        .build();

    memberService.signUp(signUpDto);
  }
}