package xeonu.bankingserver.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xeonu.bankingserver.member.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
}
