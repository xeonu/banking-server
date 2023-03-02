package xeonu.bankingserver.account.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import xeonu.bankingserver.account.dto.TransferDto;
import xeonu.bankingserver.account.entity.Account;
import xeonu.bankingserver.account.service.AccountService;


@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService accountService;

  /**
   * 계좌를 생성합니다.
   */
  @PostMapping
  @ResponseStatus(CREATED)
  public void sendRequest() {
    accountService.create();
  }

  /**
   * 특정 id의 계좌정보를 반환합니다.
   *
   * @param id account의 id
   * @return 특정 id의 계좌정보
   */
  @GetMapping
  @ResponseStatus(OK)
  public Account getAccount(@PathVariable("id") int id) {
    return accountService.getAccount(id);
  }

  /**
   * 로그인한 사용자의 모든 계좌정보를 반환합니다.
   *
   * @return 로그인한 사용자의 모든 계좌정보
   */
  @GetMapping("/my")
  @ResponseStatus(OK)
  public List<Account> getMyAccounts() {
    return accountService.getMyAccounts();
  }

  @PostMapping("/transfer")
  @ResponseStatus(OK)
  public void transfer(@RequestBody @Validated TransferDto transferDto) {
    accountService.transfer(transferDto);
  }
}
