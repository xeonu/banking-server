package xeonu.bankingserver.account.repository;

import java.util.List;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import xeonu.bankingserver.account.entity.Account;
import xeonu.bankingserver.member.entity.Member;

public interface AccountRepository extends JpaRepository<Account, Integer> {

  public Optional<Account> findByNumber(String number);

  public List<Account> findByOwner(Member owner);

  @Modifying
  @Query("UPDATE Account a SET a.balance = a.balance - ?2 WHERE a = ?1")
  void decreaseBalance(Account senderAccount, long amount);

  @Modifying
  @Query("UPDATE Account a SET a.balance = a.balance + ?2 WHERE a = ?1")
  void increaseBalance(Account receiverAccount, long amount);

  @Modifying
  @Query("UPDATE Account a SET a.balance = a.balance - ?2 WHERE a = ?1")
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  void decreaseBalanceByPessimisticLock(Account senderAccount, long amount);

  @Modifying
  @Query("UPDATE Account a SET a.balance = a.balance + ?2 WHERE a = ?1")
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  void increaseBalanceByPessimisticLock(Account receiverAccount, long amount);
}
