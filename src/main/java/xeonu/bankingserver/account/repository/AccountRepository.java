package xeonu.bankingserver.account.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import xeonu.bankingserver.account.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Integer> {

  public Optional<Account> findByNumber(String number);

  public List<Account> findByOwner_Id(int id);
}
