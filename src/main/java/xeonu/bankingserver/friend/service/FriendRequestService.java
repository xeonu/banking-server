package xeonu.bankingserver.friend.service;

import static xeonu.bankingserver.friend.entity.AcceptedStatus.ACCEPTED;
import static xeonu.bankingserver.friend.entity.AcceptedStatus.REJECTED;
import static xeonu.bankingserver.friend.entity.AcceptedStatus.WAITING;
import static xeonu.bankingserver.friend.exception.FriendErrorReponse.FRIEND_REQUEST_ALREADY_SENT;
import static xeonu.bankingserver.friend.exception.FriendErrorReponse.FRIEND_REQUEST_NOT_EXIST;
import static xeonu.bankingserver.friend.exception.FriendErrorReponse.FRIEND_REQUEST_NOT_MATCH;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xeonu.bankingserver.alarm.service.AlarmService;
import xeonu.bankingserver.common.exception.BadRequestException;
import xeonu.bankingserver.friend.entity.AcceptedStatus;
import xeonu.bankingserver.friend.entity.FriendRequest;
import xeonu.bankingserver.friend.repository.FriendRequestRepository;
import xeonu.bankingserver.member.entity.Member;
import xeonu.bankingserver.member.service.MemberService;

@Service
@RequiredArgsConstructor
public class FriendRequestService {

  private final FriendRequestRepository repository;
  private final MemberService memberService;
  private final AlarmService alarmService;
  private final FriendInfoService friendInfoService;

  /**
   * 로그인한 사용자가 특정 loginId를 가진 member에게 친구요청을 보냅니다.
   *
   * @param loginId 친구요청을 받는 member의 loginId
   */
  public void send(String loginId) {
    Member sender = memberService.getLoginMember();
    Member receiver = memberService.getMemberByLoginId(loginId);

    boolean alreadySent = repository.existsBySenderAndReceiverAndAcceptedStatus(
        sender, receiver, WAITING);
    if (alreadySent) {
      throw new BadRequestException(FRIEND_REQUEST_ALREADY_SENT.getErrorResponse());
    }

    FriendRequest friendRequest = FriendRequest.builder()
        .sender(sender)
        .receiver(receiver)
        .acceptedStatus(WAITING)
        .build();

    repository.save(friendRequest);
    alarmService.sendAlarmMessage(receiver.getId(),
        "member " + sender.getLoginId() + " send friend request!");
  }

  /**
   * 보낸 친구요청들을 확인합니다. 친구요청이 없을 경우 비어있는 List를 반환합니다.
   *
   * @return 보낸 친구요청 목록
   */
  public List<FriendRequest> getSentFriendRequests() {
    Member member = memberService.getLoginMember();

    return repository.findFriendRequestBySenderAndAcceptedStatus(member, WAITING);
  }

  /**
   * 받은 친구요청들을 확인합니다. 친구요청이 없을 경우 비어있는 List를 반환합니다.
   *
   * @return 받은 친구요청 목록
   */
  public List<FriendRequest> getReceivedFriendRequests() {
    Member member = memberService.getLoginMember();

    return repository.findFriendRequestByReceiverAndAcceptedStatus(member, WAITING);
  }

  /**
   * 특정 id의 친구요청을 반환합니다.
   *
   * @param id friendRequest의 id
   * @return 특정 id의 친구요청
   */
  public FriendRequest getFriendRequest(int id) {
    Optional<FriendRequest> friendRequest = repository.findById(id);

    if (friendRequest.isEmpty()) {
      throw new BadRequestException(FRIEND_REQUEST_NOT_EXIST.getErrorResponse());
    }

    return friendRequest.get();
  }

  /**
   * 특정 id의 친구요청 상태를 수정합니다.
   *
   * @param id             friendRequest의 id
   * @param acceptedStatus 수정할 친구요청 상태
   */
  public void setAcceptedStatus(int id, AcceptedStatus acceptedStatus) {
    Member member = memberService.getLoginMember();
    FriendRequest friendRequest = getFriendRequest(id);

    repository.updateAcceptedStatusById(friendRequest.getId(), acceptedStatus);
  }

  /**
   * 받은 친구요청을 승인합니다.
   *
   * @param id friendRequest의 id
   */
  @Transactional
  public void accept(int id) {
    Member member = memberService.getLoginMember();
    FriendRequest friendRequest = getFriendRequest(id);
    Member sender = friendRequest.getSender();

    if (friendRequest.getReceiver().getId() != member.getId()
        || friendRequest.getAcceptedStatus() != WAITING) {
      throw new BadRequestException(FRIEND_REQUEST_NOT_MATCH.getErrorResponse());
    }

    friendInfoService.add(friendRequest.getSender().getId());
    setAcceptedStatus(id, ACCEPTED);
  }

  /**
   * 받은 친구요청을 거부합니다.
   *
   * @param id friendRequest의 id
   */
  @Transactional
  public void reject(int id) {
    Member member = memberService.getLoginMember();
    FriendRequest friendRequest = getFriendRequest(id);

    if (friendRequest.getReceiver().getId() != member.getId()
        || friendRequest.getAcceptedStatus() != WAITING) {
      throw new BadRequestException(FRIEND_REQUEST_NOT_MATCH.getErrorResponse());
    }

    setAcceptedStatus(id, REJECTED);
  }
}
