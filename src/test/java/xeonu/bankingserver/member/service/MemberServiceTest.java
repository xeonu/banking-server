package xeonu.bankingserver.member.service;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import xeonu.bankingserver.common.exception.BadRequestException;
import xeonu.bankingserver.member.dto.SignUpDto;
import xeonu.bankingserver.member.entity.Member;
import xeonu.bankingserver.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

  @Mock
  private MemberRepository repository;

  @Mock
  private EncryptService encryptService;

  @InjectMocks
  private MemberService memberService;


  @Test
  @DisplayName("회원가입 성공")
  public void signUp_Success() {
    SignUpDto signUpDto = new SignUpDto("abcd1234", "password12!");
    String encryptedPassword = "encryptedPassword";

    when(memberService.existsByLoginId(signUpDto.getLoginId())).thenReturn(false);
    when(encryptService.encrypt(signUpDto.getPassword())).thenReturn(encryptedPassword);

    memberService.signUp(signUpDto);
    verify(repository).save(any(Member.class));
  }

  @Test
  @DisplayName("중복된 아이디로 회원가입")
  public void signUp_DuplicatedLoginId() {
    SignUpDto signUpDto = new SignUpDto("abcd1234", "password12!");

    when(memberService.existsByLoginId(signUpDto.getLoginId())).thenReturn(true);

    assertThrows(BadRequestException.class, () -> memberService.signUp(signUpDto));
  }

  @Test
  @DisplayName("중복된 아이디로 중복체크")
  public void loginIdDuplicateCheck_WhenLoginIdExists() {
    String loginId = "abcd1234";

    when(memberService.existsByLoginId(loginId)).thenReturn(true);

    assertThrows(BadRequestException.class,
        () -> memberService.loginIdDuplicateCheck(loginId));
  }

  @Test
  @DisplayName("중복되지 않은 아이디로 중복체크")
  public void loginIdDuplicateCheck_WhenLoginIdNotExists() {
    String loginId = "abcd1234";

    when(memberService.existsByLoginId(loginId)).thenReturn(false);

    Assertions.assertDoesNotThrow(() -> memberService.loginIdDuplicateCheck(loginId));
  }
}