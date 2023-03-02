package xeonu.bankingserver.account.service;

import static xeonu.bankingserver.account.exception.AccountErrorResponse.ACCOUNT_NOT_EXIST;
import static xeonu.bankingserver.account.exception.AccountErrorResponse.DUPLICATED_ACCOUNT_NUMBER;
import static xeonu.bankingserver.account.exception.AccountErrorResponse.NOT_ACCOUNT_OWNER;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xeonu.bankingserver.account.entity.Account;
import xeonu.bankingserver.account.repository.AccountRepository;
import xeonu.bankingserver.account.utility.AccountUtil;
import xeonu.bankingserver.common.exception.BadRequestException;
import xeonu.bankingserver.member.entity.Member;
import xeonu.bankingserver.member.service.MemberService;

@Service
@RequiredArgsConstructor
public class AccountService {

  private final AccountRepository repository;
  private final MemberService memberService;

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
    return repository.findByOwner_Id(owner.getId());
  }
}
