package xeonu.bankingserver.friend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xeonu.bankingserver.friend.entity.FriendInfo;
import xeonu.bankingserver.member.entity.Member;

public interface FriendInfoRepository extends JpaRepository<FriendInfo, Integer> {

  public boolean existsByMemberAndFriend(Member member, Member friend);
}
