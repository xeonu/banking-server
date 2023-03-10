package xeonu.bankingserver.account.service;

import static java.lang.String.valueOf;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
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
import xeonu.bankingserver.account.dto.TransferDto;
import xeonu.bankingserver.account.entity.Account;
import xeonu.bankingserver.account.repository.AccountRepository;
import xeonu.bankingserver.account.utility.AccountUtil;
import xeonu.bankingserver.alarm.service.AlarmService;
import xeonu.bankingserver.common.exception.BadRequestException;
import xeonu.bankingserver.friend.service.FriendInfoService;
import xeonu.bankingserver.member.entity.Member;
import xeonu.bankingserver.member.service.MemberService;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

  @Mock
  AccountRepository repository;

  @Mock
  MemberService memberService;

  @Mock
  FriendInfoService friendInfoService;

  @Mock
  AlarmService alarmService;

  @Mock
  TransferLockService transferLockService;

  @InjectMocks
  AccountService accountService;

  private static MockedStatic<AccountUtil> accountUtil;

  Member member = Member.builder()
      .id(1)
      .loginId("abcd1234")
      .password("password12!")
      .build();

  Member sender = Member.builder()
      .id(2)
      .loginId("send1234")
      .password("password12!")
      .build();

  Member receiver = Member.builder()
      .id(3)
      .loginId("receive1234")
      .password("password12!")
      .build();

  Account account = Account.builder()
      .owner(member)
      .number("11111111111112")
      .balance(0)
      .build();

  Account senderAccount = Account.builder()
      .id(2)
      .owner(sender)
      .number("11111111111113")
      .balance(100000)
      .build();

  Account receiverAccount = Account.builder()
      .id(3)
      .owner(receiver)
      .number("11111111111114")
      .balance(100000)
      .build();

  TransferDto transferDto = TransferDto.builder()
      .senderAccountId(senderAccount.getId())
      .receiverAccountId(receiverAccount.getId())
      .amount(1000)
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
  @DisplayName("???????????? ??????")
  public void create_Success() {
    when(memberService.getLoginMember()).thenReturn(account.getOwner());
    when(AccountUtil.randomNumberMaker(14)).thenReturn(account.getNumber());
    when(repository.findByNumber(account.getNumber())).thenReturn(Optional.empty());

    accountService.create();

    verify(repository).save(account);
  }

  @Test
  @DisplayName("??????????????? ?????? ???????????? ????????????")
  public void create_NotLogin() {
    when(memberService.getLoginMember()).thenThrow(BadRequestException.class);

    assertThrows(BadRequestException.class, () -> accountService.create());
  }

  @Test
  @DisplayName("10??? ???????????? ??????????????? ?????? ????????? ??? ????????????")
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
    when(repository.findByNumber(duplicatedNumber)).thenReturn(of(duplicatedAccount));

    assertThrows(BadRequestException.class, () -> accountService.create());
  }

  @Test
  @DisplayName("1?????? ???????????? ??????")
  public void getAccount_Success() {
    when(memberService.getLoginMember()).thenReturn(member);
    when(repository.findById(account.getId())).thenReturn(of(account));

    Account result = accountService.getAccount(account.getId());

    assertEquals(account, result);
  }

  @Test
  @DisplayName("??????????????? ?????? ???????????? 1?????? ???????????? ")
  public void getAccount_NotLogin() {
    when(memberService.getLoginMember()).thenThrow(BadRequestException.class);

    assertThrows(BadRequestException.class,
        () -> accountService.getAccount(account.getId()));
  }

  @Test
  @DisplayName("???????????? ?????? ????????? ??????")
  public void getAccount_NotExistAccount() {
    when(memberService.getLoginMember()).thenReturn(member);
    when(repository.findById(account.getId())).thenReturn(Optional.empty());

    assertThrows(BadRequestException.class,
        () -> accountService.getAccount(account.getId()));
  }

  @Test
  @DisplayName("?????? ????????? ?????? ????????? ??????")
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
    when(repository.findById(account.getId())).thenReturn(of(anotherAccount));

    assertThrows(BadRequestException.class,
        () -> accountService.getAccount(account.getId()));
  }

  @Test
  @DisplayName("???????????? ???????????? ?????? ?????? ?????? ??????")
  public void getMyAccount_Success() {
    when(memberService.getLoginMember()).thenReturn(member);

    accountService.getMyAccounts();

    verify(repository).findByOwner(member);
  }

  @Test
  @DisplayName("??????????????? ?????? ???????????????????????? ???????????? ?????? ?????? ??????")
  public void getMyAccount_NotLogin() {
    when(memberService.getLoginMember()).thenThrow(BadRequestException.class);

    assertThrows(BadRequestException.class, () -> accountService.getMyAccounts());
  }

  @Test
  @DisplayName("???????????? ??????")
  public void transfer_Succeed() {
    doNothing().when(transferLockService).lockLimitTry(5);
    when(memberService.getLoginMember()).thenReturn(sender);
    when(repository.findById(senderAccount.getId())).thenReturn(ofNullable(senderAccount));
    when(repository.findById(receiverAccount.getId())).thenReturn(ofNullable(receiverAccount));
    when(friendInfoService.isFriend(receiver)).thenReturn(true);
    doNothing().when(transferLockService).unLock();

    accountService.transfer(transferDto);

    verify(repository).decreaseBalance(senderAccount, transferDto.getAmount());
    verify(repository).increaseBalance(receiverAccount, transferDto.getAmount());
    verify(alarmService).sendAlarmMessage(receiverAccount.getId(),
        valueOf(transferDto.getAmount()) + "?????? ?????????????????????.");
  }

  @Test
  @DisplayName("??????????????? ?????? ???????????? ????????????")
  public void transfer_NotLogin() {
    doNothing().when(transferLockService).lockLimitTry(5);
    when(memberService.getLoginMember()).thenThrow(BadRequestException.class);

    assertThrows(BadRequestException.class, () -> accountService.transfer(transferDto));
  }

  @Test
  @DisplayName("??????????????? ???????????? ?????? ??? ????????????")
  public void transfer_NotExistSenderAccount() {
    doNothing().when(transferLockService).lockLimitTry(5);
    when(memberService.getLoginMember()).thenReturn(sender);
    when(repository.findById(senderAccount.getId())).thenThrow(BadRequestException.class);

    assertThrows(BadRequestException.class, () -> accountService.transfer(transferDto));
  }

  @Test
  @DisplayName("??????????????? ???????????? ?????? ??? ????????????")
  public void transfer_NotExistReceiverAccount() {
    doNothing().when(transferLockService).lockLimitTry(5);
    when(memberService.getLoginMember()).thenReturn(sender);
    when(repository.findById(senderAccount.getId())).thenReturn(ofNullable(senderAccount));
    when(repository.findById(receiverAccount.getId())).thenThrow(BadRequestException.class);

    assertThrows(BadRequestException.class, () -> accountService.transfer(transferDto));
  }

  @Test
  @DisplayName("???????????? ???????????? ??????????????? ?????? ??? ????????????")
  public void transfer_NotFriend() {
    doNothing().when(transferLockService).lockLimitTry(5);
    when(memberService.getLoginMember()).thenReturn(sender);
    when(repository.findById(senderAccount.getId())).thenReturn(ofNullable(senderAccount));
    when(repository.findById(receiverAccount.getId())).thenReturn(ofNullable(receiverAccount));
    when(friendInfoService.isFriend(receiver)).thenReturn(false);

    assertThrows(BadRequestException.class, () -> accountService.transfer(transferDto));
  }

  @Test
  @DisplayName("???????????? ???????????? ???????????? ??? ??? ????????????")
  public void transfer_TransferMoreThanBalance() {
    TransferDto transferDtoMuchAmount = TransferDto.builder()
        .senderAccountId(senderAccount.getId())
        .receiverAccountId(receiverAccount.getId())
        .amount(99999999999999L)
        .build();

    doNothing().when(transferLockService).lockLimitTry(5);
    when(memberService.getLoginMember()).thenReturn(sender);
    when(repository.findById(senderAccount.getId())).thenReturn(ofNullable(senderAccount));
    when(repository.findById(receiverAccount.getId())).thenReturn(ofNullable(receiverAccount));
    when(friendInfoService.isFriend(receiver)).thenReturn(true);

    assertThrows(BadRequestException.class, () -> accountService.transfer(transferDtoMuchAmount));
  }
}