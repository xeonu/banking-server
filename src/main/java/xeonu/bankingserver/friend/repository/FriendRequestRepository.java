package xeonu.bankingserver.friend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import xeonu.bankingserver.friend.entity.AcceptedStatus;
import xeonu.bankingserver.friend.entity.FriendRequest;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Integer> {

  boolean existsBySender_IdAndReceiver_Id(int senderId, int receiverId);

  public List<FriendRequest> findFriendRequestBySender_IdAndAcceptedStatus(int senderId,
      AcceptedStatus acceptedStatus);

  public List<FriendRequest> findFriendRequestByReceiver_IdAndAcceptedStatus(int receiverId,
      AcceptedStatus acceptedStatus);

  @Modifying
  @Query("UPDATE FriendRequest f SET f.acceptedStatus = ?2 WHERE f.id = ?1")
  public void updateAcceptedStatusById(int friendRequestId, AcceptedStatus acceptedStatus);
}
