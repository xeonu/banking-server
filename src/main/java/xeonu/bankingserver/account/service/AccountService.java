package xeonu.bankingserver.account.service;

import static java.lang.String.valueOf;
import static xeonu.bankingserver.account.exception.AccountErrorResponse.ACCOUNT_NOT_EXIST;
import static xeonu.bankingserver.account.exception.AccountErrorResponse.DUPLICATED_ACCOUNT_NUMBER;
import static xeonu.bankingserver.account.exception.AccountErrorResponse.NOT_ACCOUNT_OWNER;
import static xeonu.bankingserver.account.exception.AccountErrorResponse.NOT_ENOUGH_BALANCE;
import static xeonu.bankingserver.account.exception.AccountErrorResponse.ONLY_FRIEND_CAN_TRANSFER;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xeonu.bankingserver.account.dto.TransferDto;
import xeonu.bankingserver.account.entity.Account;
import xeonu.bankingserver.account.repository.AccountRepository;
import xeonu.bankingserver.account.utility.AccountUtil;
import xeonu.bankingserver.alarm.service.AlarmService;
import xeonu.bankingserver.common.exception.BadRequestException;
import xeonu.bankingserver.friend.service.FriendInfoService;
import xeonu.bankingserver.member.entity.Member;
import xeonu.bankingserver.member.service.MemberService;

@Service
@RequiredArgsConstructor
public class AccountService {

  private final AccountRepository repository;
  private final MemberService memberService;
  private final FriendInfoService friendInfoService;
  private final AlarmService alarmService;
  private final TransferLockService transferLockService;

  /**
   * 계좌를 생성합니다.
   */
  public void create() {
    Member member = memberService.getLoginMember();

    Account account = Account.builder()
        .owner(member)
        .number(accountNumberMaker())
        .balance(0)
        .build();

    repository.save(account);
  }

  /**
   * 14자리 계좌번호를 생성합니다. 계좌번호는 동시성 문제 해결을 위해 ThreadLocalRandom을 사용하고 DB에 존재하는 계좌번호인지 확인합니다. 최대 10번까지
   * 조회하고 10번 연속으로 중복된 계좌번호가 존재하면 예외를 던집니다. 확률상 10번 연속으로 중복된 계좌번호가 나올 확률은 희박합니다.
   *
   * @return 새로운 계좌번호
   */
  public String accountNumberMaker() {
    int length = 14;

    for (int i = 0; i < 10; i++) {
      String number = AccountUtil.randomNumberMaker(length);
      Optional<Account> account = repository.findByNumber(number);

      if (account.isPresent()) {
        continue;
      }
      return number;
    }

    throw new BadRequestException(DUPLICATED_ACCOUNT_NUMBER.getErrorResponse());
  }

  /**
   * 특정 id의 계좌 정보를 반환합니다.
   *
   * @param id account의 id
   * @return 특정 id의 계좌 정보
   */
  public Account getAccount(int id) {
    Member member = memberService.getLoginMember();

    Optional<Account> account = repository.findById(id);
    if (account.isEmpty()) {
      throw new BadRequestException(ACCOUNT_NOT_EXIST.getErrorResponse());
    }
    if (account.get().getOwner() != member) {
      throw new BadRequestException(NOT_ACCOUNT_OWNER.getErrorResponse());
    }

    return account.get();
  }

  /**
   * 로그인한 사용자의 모든 계좌정보를 반환합니다. 계좌정보가 존재하지 않는다면 비어있는 List를 반환합니다.
   *
   * @return 로그인한 사용자의 모든 계좌정보
   */
  public List<Account> getMyAccounts() {
    Member owner = memberService.getLoginMember();
    return repository.findByOwner(owner);
  }

  /**
   * 특정 id의 계좌 정보를 반환합니다.
   *
   * @param accountId 친구의 account의 id
   * @return 특정 id의 계좌 정보
   */
  public Account getFriendAccount(int accountId) {
    Member loginMember = memberService.getLoginMember();

    Optional<Account> account = repository.findById(accountId);
    if (account.isEmpty()) {
      throw new BadRequestException(ACCOUNT_NOT_EXIST.getErrorResponse());
    }
    if (!friendInfoService.isFriend(account.get().getOwner())) {
      throw new BadRequestException(NOT_ACCOUNT_OWNER.getErrorResponse());
    }

    return account.get();
  }

  /**
   * 친구의 계좌로 이체합니다. 해당 메소드는 redis를 이용하여 동시성 처리를 진행했습니다.
   *
   * @param transferDto 송금계좌, 입금계좌, 이체액을 포함한 정보
   */
  @Transactional
  public void transferLockByRedis(TransferDto transferDto) {
    int senderAccountId = transferDto.getSenderAccountId();
    int receiverAccountId = transferDto.getReceiverAccountId();
    long amount = transferDto.getAmount();

    transferLockService.lockLimitTry(5);

    try {
      Account senderAccount = getAccount(senderAccountId);
      Account receiverAccount = getFriendAccount(receiverAccountId);
      Member receiver = receiverAccount.getOwner();

      boolean isFriend = friendInfoService.isFriend(receiver);

      if (!isFriend) {
        throw new BadRequestException(ONLY_FRIEND_CAN_TRANSFER.getErrorResponse());
      }

      if (senderAccount.getBalance() < amount) {
        throw new BadRequestException(NOT_ENOUGH_BALANCE.getErrorResponse());
      }

      repository.decreaseBalance(senderAccount, amount);
      repository.increaseBalance(receiverAccount, amount);
    } finally {
      transferLockService.unLock();
      alarmService.sendAlarmMessage(receiverAccountId,
          valueOf(amount) + "원이 입금되었습니다.");
    }
  }

  /**
   * 친구의 계좌로 이체합니다. 해당 메소드는 mysql의 pessimistic Lock을 이용하여 동시성 처리를 진행했습니다.
   *
   * @param transferDto 송금계좌, 입금계좌, 이체액을 포함한 정보
   */
  @Transactional
  public void transferLockByPessimisticLock(TransferDto transferDto) {
    int senderAccountId = transferDto.getSenderAccountId();
    int receiverAccountId = transferDto.getReceiverAccountId();
    long amount = transferDto.getAmount();

    try {
      Account senderAccount = getAccount(senderAccountId);
      Account receiverAccount = getFriendAccount(receiverAccountId);
      Member receiver = receiverAccount.getOwner();

      boolean isFriend = friendInfoService.isFriend(receiver);

      if (!isFriend) {
        throw new BadRequestException(ONLY_FRIEND_CAN_TRANSFER.getErrorResponse());
      }

      if (senderAccount.getBalance() < amount) {
        throw new BadRequestException(NOT_ENOUGH_BALANCE.getErrorResponse());
      }

      repository.decreaseBalanceByPessimisticLock(senderAccount, amount);
      repository.increaseBalanceByPessimisticLock(receiverAccount, amount);
    } finally {
      alarmService.sendAlarmMessage(receiverAccountId,
          valueOf(amount) + "원이 입금되었습니다.");
    }
  }
}
