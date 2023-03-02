package xeonu.bankingserver.friend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import xeonu.bankingserver.friend.entity.AcceptedStatus;
import xeonu.bankingserver.friend.entity.FriendRequest;
import xeonu.bankingserver.member.entity.Member;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Integer> {

  boolean existsBySenderAndReceiverAndAcceptedStatus(Member sender, Member receiver,
      AcceptedStatus acceptedStatus);

  public List<FriendRequest> findFriendRequestBySenderAndAcceptedStatus(Member sender,
      AcceptedStatus acceptedStatus);

  public List<FriendRequest> findFriendRequestByReceiverAndAcceptedStatus(Member receiver,
      AcceptedStatus acceptedStatus);

  @Modifying
  @Query("UPDATE FriendRequest f SET f.acceptedStatus = ?2 WHERE f.id = ?1")
  public void updateAcceptedStatusById(int friendRequestId, AcceptedStatus acceptedStatus);
}
