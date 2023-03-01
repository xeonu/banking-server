package xeonu.bankingserver.friend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xeonu.bankingserver.friend.entity.FriendInfo;

public interface FriendInfoRepository extends JpaRepository<FriendInfo, Integer> {

  public boolean existsByMember_IdAndFriend_Id(int memberId, int friendId);
}
