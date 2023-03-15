package xeonu.bankingserver.friend.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import xeonu.bankingserver.friend.entity.FriendRequest;
import xeonu.bankingserver.friend.service.FriendRequestService;


@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {

  private final FriendRequestService friendRequestService;

  /**
   * 특정 loginId를 가진 member에게 친구요청을 보냅니다.
   *
   * @param loginId 친구요청을 보내려는 loginId
   */
  @PostMapping("/requests/send")
  @ResponseStatus(CREATED)
  public void sendRequest(@RequestParam("friend") String loginId) {
    friendRequestService.send(loginId);
  }

  /**
   * 친구요청을 수락합니다.
   *
   * @param id friendRequest의 id
   */
  @PostMapping("/requests/accept")
  @ResponseStatus(OK)
  public void acceptRequest(@RequestParam("friend-request-id") int id) {
    friendRequestService.accept(id);
  }

  /**
   * 친구요청을 거절합니다.
   *
   * @param id friendRequest의 id
   */
  @PostMapping("/requests/reject")
  @ResponseStatus(OK)
  public void rejectRequest(@RequestParam("friend-request-id") int id) {
    friendRequestService.reject(id);
  }

  /**
   * 보낸 친구요청들 중 아직 수락 혹은 거절하지 않고 대기중인 요청들을 반환합니다.
   *
   * @return 보낸 friendRequest 목록
   */
  @GetMapping("/requests/sent")
  @ResponseStatus(OK)
  public List<FriendRequest> getSentRequests() {
    return friendRequestService.getSentFriendRequests();
  }

  /**
   * 받은 친구요청들 중 아직 수락 혹은 거절하지 않고 대기중인 요청들을 반환합니다.
   *
   * @return 받은 friendRequest 목록
   */
  @GetMapping("/requests/received")
  @ResponseStatus(OK)
  public List<FriendRequest> getReceivedRequests() {
    return friendRequestService.getReceivedFriendRequests();
  }
}
