package xeonu.bankingserver.member.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import xeonu.bankingserver.member.dto.SignUpDto;
import xeonu.bankingserver.member.service.MemberService;


@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;

  /**
   * 회원가입을 수행합니다.
   *
   * @param signUpDto 아이디와 비밀번호가 포함된 정보
   */
  @PostMapping("/sign-up")
  @ResponseStatus(CREATED)
  public void signUp(@RequestBody @Validated SignUpDto signUpDto) {
    memberService.signUp(signUpDto);
  }

  /**
   * 아이디 중복검사를 시행합니다.
   *
   * @param loginId 회원의 로그인 아이디
   */
  @GetMapping("/login-id/exists")
  @ResponseStatus(OK)
  public void loginIdDuplicatedCheck(@RequestParam("login-id") String loginId) {
    memberService.loginIdDuplicateCheck(loginId);
  }
}
