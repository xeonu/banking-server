package xeonu.bankingserver.friend.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static xeonu.bankingserver.friend.entity.AcceptedStatus.ACCEPTED;
import static xeonu.bankingserver.friend.entity.AcceptedStatus.REJECTED;
import static xeonu.bankingserver.friend.entity.AcceptedStatus.WAITING;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import xeonu.bankingserver.alarm.service.AlarmService;
import xeonu.bankingserver.common.exception.BadRequestException;
import xeonu.bankingserver.friend.entity.FriendRequest;
import xeonu.bankingserver.friend.repository.FriendRequestRepository;
import xeonu.bankingserver.member.entity.Member;
import xeonu.bankingserver.member.service.MemberService;

@ExtendWith(MockitoExtension.class)
class FriendRequestServiceTest {

  @Mock
  FriendRequestRepository repository;

  @Mock
  MemberService memberService;

  @Mock
  AlarmService alarmService;

  @Mock
  FriendInfoService friendInfoService;

  @InjectMocks
  FriendRequestService friendRequestService;

  Member sender = Member.builder().id(1).loginId("sender1234").password("password12!").build();

  Member receiver = Member.builder().id(2).loginId("receiver1234").password("password12!").build();

  @Test
  @DisplayName("친구요청 전송 성공")
  public void send_Success() {
    when(memberService.getLoginMember()).thenReturn(sender);
    when(memberService.getMemberByLoginId(receiver.getLoginId())).thenReturn(receiver);
    when(repository.existsBySender_IdAndReceiver_Id(sender.getId(), receiver.getId())).thenReturn(
        false);
    doNothing().when(alarmService).sendAlarmMessage(receiver.getId(),
        "member " + sender.getLoginId() + " send friend request!");

    friendRequestService.send(receiver.getLoginId());

    verify(repository).save(any());
  }

  @Test
  @DisplayName("로그인 하지 않은 상태에서 친구요청 전송")
  public void send_NotLogin() {
    when(memberService.getLoginMember()).thenThrow(BadRequestException.class);

    assertThrows(BadRequestException.class, () -> friendRequestService.send(receiver.getLoginId()));
  }

  @Test
  @DisplayName("존재하지 않은 사용자에게 친구요청 전송")
  public void send_NotExistFriend() {
    when(memberService.getLoginMember()).thenReturn(sender);
    when(memberService.getMemberByLoginId(receiver.getLoginId())).thenThrow(
        BadRequestException.class);

    assertThrows(BadRequestException.class, () -> friendRequestService.send(receiver.getLoginId()));
  }

  @Test
  @DisplayName("이미 친구요청을 전송한 상대에게 친구요청 전송")
  public void send_AlreadySentFriend() {
    when(memberService.getLoginMember()).thenReturn(sender);
    when(memberService.getMemberByLoginId(receiver.getLoginId())).thenReturn(receiver);
    when(repository.existsBySender_IdAndReceiver_Id(sender.getId(), receiver.getId())).thenReturn(
        true);

    assertThrows(BadRequestException.class, () -> friendRequestService.send(receiver.getLoginId()));
  }

  @Test
  @DisplayName("보낸 친구요청 목록 반환")
  public void getSentFriendRequests_Succeed() {
    when(memberService.getLoginMember()).thenReturn(sender);

    friendRequestService.getSentFriendRequests();

    verify(repository).findFriendRequestBySender_IdAndAcceptedStatus(sender.getId(), WAITING);
  }

  @Test
  @DisplayName("로그인 하지 않은 상태에서 보낸 친구요청 목록 반환")
  public void getSentFriendRequests_NotLogin() {
    when(memberService.getLoginMember()).thenThrow(BadRequestException.class);

    assertThrows(BadRequestException.class, () -> friendRequestService.getSentFriendRequests());
  }

  @Test
  @DisplayName("받은 친구요청 목록 반환")
  public void getReceivedFriendRequests_Succeed() {
    when(memberService.getLoginMember()).thenReturn(receiver);

    friendRequestService.getReceivedFriendRequests();

    verify(repository).findFriendRequestByReceiver_IdAndAcceptedStatus(receiver.getId(), WAITING);
  }

  @Test
  @DisplayName("로그인 하지 않은 상태에서 받은 친구요청 목록 반환")
  public void getReceivedFriendRequests_NotLogin() {
    when(memberService.getLoginMember()).thenThrow(BadRequestException.class);

    assertThrows(BadRequestException.class, () -> friendRequestService.getReceivedFriendRequests());
  }

  @Test
  @DisplayName("친구요청 승인 성공")
  public void accept_Success() {
    FriendRequest friendRequest = FriendRequest.builder().id(1).sender(sender).receiver(receiver)
        .acceptedStatus(WAITING).build();

    when(memberService.getLoginMember()).thenReturn(receiver);
    when(repository.findById(friendRequest.getId())).thenReturn(Optional.of(friendRequest));
    doNothing().when(repository).updateAcceptedStatusById(friendRequest.getId(), ACCEPTED);

    friendRequestService.accept(friendRequest.getId());

    verify(friendInfoService).add(friendRequest.getSender().getId());
  }

  @Test
  @DisplayName("로그인 하지 않은 상태에서 친구요청 승인")
  public void accept_NotLogin() {
    FriendRequest friendRequest = FriendRequest.builder().id(1).sender(sender).receiver(receiver)
        .acceptedStatus(WAITING).build();

    when(memberService.getLoginMember()).thenThrow(BadRequestException.class);
    assertThrows(BadRequestException.class,
        () -> friendRequestService.accept(friendRequest.getId()));
  }

  @Test
  @DisplayName("존재하지 않은 친구요청정보로 친구요청 승인")
  public void accept_NotExistFriendRequest() {
    FriendRequest friendRequest = FriendRequest.builder().id(1).sender(sender).receiver(receiver)
        .acceptedStatus(WAITING).build();

    when(memberService.getLoginMember()).thenReturn(receiver);
    when(repository.findById(friendRequest.getId())).thenReturn(Optional.empty());

    assertThrows(BadRequestException.class,
        () -> friendRequestService.accept(friendRequest.getId()));
  }

  @Test
  @DisplayName("이미 수락된 친구요청정보로 친구요청 승인")
  public void accept_AcceptedFriendRequest() {
    FriendRequest friendRequest = FriendRequest.builder().id(1).sender(sender).receiver(receiver)
        .acceptedStatus(ACCEPTED).build();

    when(memberService.getLoginMember()).thenReturn(receiver);
    when(repository.findById(friendRequest.getId())).thenReturn(Optional.of(friendRequest));

    assertThrows(BadRequestException.class,
        () -> friendRequestService.accept(friendRequest.getId()));
  }

  @Test
  @DisplayName("이미 거절한 친구요청정보로 친구요청 승인")
  public void accept_RejectedFriendRequest() {
    FriendRequest friendRequest = FriendRequest.builder().id(1).sender(sender).receiver(receiver)
        .acceptedStatus(REJECTED).build();

    when(memberService.getLoginMember()).thenReturn(receiver);
    when(repository.findById(friendRequest.getId())).thenReturn(Optional.of(friendRequest));

    assertThrows(BadRequestException.class,
        () -> friendRequestService.accept(friendRequest.getId()));
  }

  @Test
  @DisplayName("친구요청 거절")
  public void reject_Success() {
    FriendRequest friendRequest = FriendRequest.builder().id(1).sender(sender).receiver(receiver)
        .acceptedStatus(WAITING).build();

    when(memberService.getLoginMember()).thenReturn(receiver);
    when(repository.findById(friendRequest.getId())).thenReturn(Optional.of(friendRequest));

    friendRequestService.reject(friendRequest.getId());

    verify(repository).updateAcceptedStatusById(friendRequest.getId(), REJECTED);
  }

  @Test
  @DisplayName("로그인 하지 않은 상태에서 친구요청 승인")
  public void reject_NotLogin() {
    FriendRequest friendRequest = FriendRequest.builder().id(1).sender(sender).receiver(receiver)
        .acceptedStatus(WAITING).build();

    when(memberService.getLoginMember()).thenThrow(BadRequestException.class);
    assertThrows(BadRequestException.class,
        () -> friendRequestService.reject(friendRequest.getId()));
  }

  @Test
  @DisplayName("존재하지 않은 친구요청정보로 친구요청")
  public void reject_NotExistFriendRequest() {
    FriendRequest friendRequest = FriendRequest.builder().id(1).sender(sender).receiver(receiver)
        .acceptedStatus(WAITING).build();

    when(memberService.getLoginMember()).thenReturn(receiver);
    when(repository.findById(friendRequest.getId())).thenReturn(Optional.empty());

    assertThrows(BadRequestException.class,
        () -> friendRequestService.reject(friendRequest.getId()));
  }

  @Test
  @DisplayName("이미 수락된 친구요청정보로 친구요청")
  public void reject_AcceptedFriendRequest() {
    FriendRequest friendRequest = FriendRequest.builder().id(1).sender(sender).receiver(receiver)
        .acceptedStatus(ACCEPTED).build();

    when(memberService.getLoginMember()).thenReturn(receiver);
    when(repository.findById(friendRequest.getId())).thenReturn(Optional.of(friendRequest));

    assertThrows(BadRequestException.class,
        () -> friendRequestService.reject(friendRequest.getId()));
  }

  @Test
  @DisplayName("이미 거절한 친구요청정보로 친구요청")
  public void reject_RejectedFriendRequest() {
    FriendRequest friendRequest = FriendRequest.builder().id(1).sender(sender).receiver(receiver)
        .acceptedStatus(REJECTED).build();

    when(memberService.getLoginMember()).thenReturn(receiver);
    when(repository.findById(friendRequest.getId())).thenReturn(Optional.of(friendRequest));

    assertThrows(BadRequestException.class,
        () -> friendRequestService.reject(friendRequest.getId()));
  }
}