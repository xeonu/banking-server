package xeonu.bankingserver.account.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import xeonu.bankingserver.account.entity.Account;
import xeonu.bankingserver.account.repository.AccountRepository;
import xeonu.bankingserver.account.utility.AccountUtil;
import xeonu.bankingserver.common.exception.BadRequestException;
import xeonu.bankingserver.member.entity.Member;
import xeonu.bankingserver.member.service.MemberService;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

  @Mock
  AccountRepository repository;

  @Mock
  MemberService memberService;

  @InjectMocks
  AccountService accountService;

  private static MockedStatic<AccountUtil> accountUtil;

  Member member = Member.builder()
      .id(1)
      .loginId("abcd1234")
      .password("password12!")
      .build();

  Account account = Account.builder()
      .owner(member)
      .number("11111111111111")
      .balance(0)
      .build();

  @BeforeAll
  public static void beforeAll() {
    accountUtil = mockStatic(AccountUtil.class);
  }

  @AfterAll
  public static void afterAll() {
    accountUtil.close();
  }

  @Test
  @DisplayName("계좌생성 성공")
  public void create_Success() {
    when(memberService.getLoginMember()).thenReturn(member);
    when(repository.findByNumber(anyString())).thenReturn(Optional.empty());
    when(AccountUtil.randomNumberMaker(14)).thenReturn(account.getNumber());
    when(repository.findByNumber(account.getNumber())).thenReturn(Optional.empty());

    accountService.create();

    verify(repository).save(account);
  }

  @Test
  @DisplayName("로그인하지 않은 상태에서 계좌생성")
  public void create_NotLogin() {
    when(memberService.getLoginMember()).thenThrow(BadRequestException.class);

    assertThrows(BadRequestException.class, () -> accountService.create());
  }

  @Test
  @DisplayName("10번 연속으로 계좌번호가 이미 존재할 때 계좌생성")
  public void create_DuplicatedNumber10Times() {
    Account duplicatedAccount = Account.builder()
        .owner(member)
        .number("11111111111111")
        .balance(0)
        .build();

    String duplicatedNumber = new String("1234567812345");

    when(memberService.getLoginMember()).thenReturn(member);
    when(repository.findByNumber(anyString())).thenReturn(Optional.empty());
    when(AccountUtil.randomNumberMaker(14)).thenReturn(duplicatedNumber);
    when(repository.findByNumber(duplicatedNumber)).thenReturn(Optional.of(duplicatedAccount));

    assertThrows(BadRequestException.class, () -> accountService.create());
  }

  @Test
  @DisplayName("1개의 계좌조회 성공")
  public void getAccount_Success() {
    when(memberService.getLoginMember()).thenReturn(member);
    when(repository.findById(account.getId())).thenReturn(Optional.of(account));

    Account result = accountService.getAccount(account.getId());

    assertEquals(account, result);
  }

  @Test
  @DisplayName("로그인하지 않은 상태에서 1개의 계좌조회 ")
  public void getAccount_NotLogin() {
    when(memberService.getLoginMember()).thenThrow(BadRequestException.class);

    assertThrows(BadRequestException.class,
        () -> accountService.getAccount(account.getId()));
  }

  @Test
  @DisplayName("존재하지 않은 계좌를 조회")
  public void getAccount_NotExistAccount() {
    when(memberService.getLoginMember()).thenReturn(member);
    when(repository.findById(account.getId())).thenReturn(Optional.empty());

    assertThrows(BadRequestException.class,
        () -> accountService.getAccount(account.getId()));
  }

  @Test
  @DisplayName("본인 소유가 아닌 계좌를 조회")
  public void getAccount_NotMyAccount() {
    Member another = Member.builder()
        .id(2)
        .loginId("frog1234")
        .password("password123!")
        .build();
    Account anotherAccount = Account.builder()
        .owner(another)
        .number("22222222222222")
        .balance(0)
        .build();

    when(memberService.getLoginMember()).thenReturn(member);
    when(repository.findById(account.getId())).thenReturn(Optional.of(anotherAccount));

    assertThrows(BadRequestException.class,
        () -> accountService.getAccount(account.getId()));
  }

  @Test
  @DisplayName("로그인한 사용자의 모든 계좌 조회 성공")
  public void getMyAccount_Success() {
    when(memberService.getLoginMember()).thenReturn(member);

    accountService.getMyAccounts();

    verify(repository).findByOwner_Id(member.getId());
  }

  @Test
  @DisplayName("로그인하지 않은 상태에서로그인한 사용자의 모든 계좌 조회")
  public void getMyAccount_NotLogin() {
    when(memberService.getLoginMember()).thenThrow(BadRequestException.class);

    assertThrows(BadRequestException.class, () -> accountService.getMyAccounts());
  }
}