package xeonu.bankingserver.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xeonu.bankingserver.account.entity.AccountTransfer;

public interface AccountTransferRepository extends JpaRepository<AccountTransfer, Integer> {

}
