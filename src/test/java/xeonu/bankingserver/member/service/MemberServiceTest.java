package xeonu.bankingserver.member.service;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import xeonu.bankingserver.common.exception.BadRequestException;
import xeonu.bankingserver.member.dto.LoginDto;
import xeonu.bankingserver.member.dto.SignUpDto;
import xeonu.bankingserver.member.entity.Member;
import xeonu.bankingserver.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

  @Mock
  private MemberRepository repository;

  @Mock
  private EncryptService encryptService;

  @Mock
  private LoginService loginService;

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

  @Test
  @DisplayName("정상적인 로그인")
  public void login_Success() {
    LoginDto loginDto = new LoginDto("abcd1234", "password12!");
    String loginId = loginDto.getLoginId();
    String plainPassword = loginDto.getPassword();

    Member storedMember = Member.builder().
        id(1).
        loginId("abcd1234").
        password("encrypted12!").
        build();

    when(repository.existsMemberByLoginId(loginId)).thenReturn(true);
    when(repository.findByLoginId(loginId)).thenReturn(Optional.of(storedMember));
    when(encryptService.checkPassword(plainPassword, storedMember.getPassword())).
        thenReturn(true);

    memberService.login(loginDto);

    verify(loginService).login(1);
  }

  @Test
  @DisplayName("잘못된 비밀번호를 이용한 로그인")
  public void login_PasswordNotMatch() {
    LoginDto loginDto = new LoginDto("abcd1234", "password12!");
    String loginId = loginDto.getLoginId();
    String plainPassword = loginDto.getPassword();

    Member storedMember = Member.builder().
        id(1).
        loginId("abcd1234").
        password("encrypted12!").
        build();

    when(repository.existsMemberByLoginId(loginId)).thenReturn(true);
    when(repository.findByLoginId(loginId)).thenReturn(Optional.of(storedMember));
    when(encryptService.checkPassword(plainPassword, storedMember.getPassword())).
        thenReturn(false);

    Assertions.assertThrows(BadRequestException.class, () -> memberService.login(loginDto));
  }

  @Test
  @DisplayName("존재하지 않은 회원 아이디를 이용한 로그인")
  public void login_LoginIdNotExist() {
    LoginDto loginDto = new LoginDto("abcd1234", "password12!");
    String loginId = loginDto.getLoginId();
    String plainPassword = loginDto.getPassword();

    Member storedMember = Member.builder().
        id(1).
        loginId("abcd1234").
        password("encrypted12!").
        build();

    when(repository.existsMemberByLoginId(loginId)).thenReturn(false);

    Assertions.assertThrows(BadRequestException.class, () -> memberService.login(loginDto));
  }

  @Test
  @DisplayName("정상적인 로그아웃")
  public void logout_Success() {
    memberService.logout();

    verify(loginService).logout();
  }
}