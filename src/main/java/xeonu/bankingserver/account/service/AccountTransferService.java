package xeonu.bankingserver.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xeonu.bankingserver.account.entity.Account;
import xeonu.bankingserver.account.entity.AccountTransfer;
import xeonu.bankingserver.account.repository.AccountTransferRepository;

@Service
@RequiredArgsConstructor
public class AccountTransferService {

  private final AccountTransferRepository repository;

  /**
   * 계좌이체에 대한 기록을 작성합니다. 해당 메소드는 계좌와 계좌이체에 관한 검증을 진행하지 않고 기록만 진행합니다. 이 메소드를 사용할 때 꼭 계좌와 계좌이체에 관한 검증을
   * 진행해야합니다.
   *
   * @param senderAccount   송금계좌정보
   * @param receiverAccount 입금계좌정보
   * @param amount          이체금액
   */
  public void add(Account senderAccount, Account receiverAccount, long amount) {
    AccountTransfer accountTransfer = AccountTransfer.builder()
        .senderAccount(senderAccount)
        .receiverAccount(receiverAccount)
        .amount(amount)
        .build();

    repository.save(accountTransfer);
  }
}
