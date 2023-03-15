package xeonu.bankingserver.friend.service;

import static xeonu.bankingserver.friend.exception.FriendErrorReponse.FRIEND_ALREADY;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xeonu.bankingserver.common.exception.BadRequestException;
import xeonu.bankingserver.friend.entity.FriendInfo;
import xeonu.bankingserver.friend.repository.FriendInfoRepository;
import xeonu.bankingserver.member.entity.Member;
import xeonu.bankingserver.member.service.MemberService;

@Service
@RequiredArgsConstructor
@EqualsAndHashCode
public class FriendInfoService {

  private final FriendInfoRepository repository;
  private final MemberService memberService;

  /**
   * 특정 member를 친구로 추가합니다. 해당 메소드는 꼭 friendRequest를 통해서 처리되어야 하고 단독적으로 처리되면 안됩니다.
   *
   * @param friendId 친구로 추가할 member의 id
   */
  @Transactional
  public void add(int friendId) {
    Member loginMember = memberService.getLoginMember();
    Member friend = memberService.getMemberById(friendId);
    boolean alreadyFriend = repository.existsByMemberAndFriend(loginMember, friend);
    boolean alreadyFriendReverse = repository.existsByMemberAndFriend(friend, loginMember);

    if (alreadyFriend || alreadyFriendReverse) {
      throw new BadRequestException(FRIEND_ALREADY.getErrorResponse());
    }

    FriendInfo friendInfo = FriendInfo.builder()
        .member(loginMember)
        .friend(friend)
        .build();

    FriendInfo friendInfoReverse = FriendInfo.builder()
        .member(friend)
        .friend(loginMember)
        .build();

    repository.save(friendInfo);
    repository.save(friendInfoReverse);
  }

  /**
   * 로그인한 사용자와 특정 아이디의 사용자가 친구관계인지 확인합니다. 이 메소드는 로그인한 사용자의 친구만 확인 가능합니다.
   *
   * @param friend 친구의 정보
   * @return 로그인한 사용자와 파라미터로 넘어온 사용자와의 친구여부
   */
  public boolean isFriend(Member friend) {
    Member loginMember = memberService.getLoginMember();

    return repository.existsByMemberAndFriend(loginMember, friend);
  }
}
