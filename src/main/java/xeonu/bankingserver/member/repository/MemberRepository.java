package xeonu.bankingserver.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xeonu.bankingserver.member.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {

  public boolean existsMemberByLoginId(String loginId);

  public Optional<Member> findByLoginId(String loginId);
}
