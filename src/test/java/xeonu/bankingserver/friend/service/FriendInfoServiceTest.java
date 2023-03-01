package xeonu.bankingserver.friend.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import xeonu.bankingserver.common.exception.BadRequestException;
import xeonu.bankingserver.friend.entity.FriendInfo;
import xeonu.bankingserver.friend.repository.FriendInfoRepository;
import xeonu.bankingserver.member.entity.Member;
import xeonu.bankingserver.member.service.MemberService;

@ExtendWith(MockitoExtension.class)
class FriendInfoServiceTest {

  @Mock
  FriendInfoRepository repository;

  @Mock
  MemberService memberService;

  @InjectMocks
  FriendInfoService friendInfoService;

  Member loginMember = Member.builder()
      .id(1)
      .loginId("abcd1234")
      .password("password12!").build();

  Member friend = Member.builder()
      .id(2)
      .loginId("friend1234")
      .password("password12!").build();

  @Test
  @DisplayName("친구추가 성공")
  public void add_Success() {
    when(memberService.getLoginMember()).thenReturn(loginMember);
    when(memberService.getMemberById(friend.getId())).thenReturn(friend);
    when(repository.existsByMember_IdAndFriend_Id(loginMember.getId(), friend.getId()))
        .thenReturn(false);
    when(repository.existsByMember_IdAndFriend_Id(friend.getId(), loginMember.getId()))
        .thenReturn(false);

    friendInfoService.add(friend.getId());

    FriendInfo friendInfo = FriendInfo.builder()
        .member(loginMember)
        .friend(friend)
        .build();

    FriendInfo friendInfoReverse = FriendInfo.builder()
        .member(friend)
        .friend(loginMember)
        .build();

    verify(repository).save(friendInfo);
    verify(repository).save(friendInfoReverse);
  }

  @Test
  @DisplayName("이미 존재하는 친구일 때 친구추가 실패")
  public void add_AlreadyFriend() {
    when(memberService.getLoginMember()).thenReturn(loginMember);
    when(memberService.getMemberById(friend.getId())).thenReturn(friend);
    when(repository.existsByMember_IdAndFriend_Id(loginMember.getId(), friend.getId()))
        .thenReturn(true);
    when(repository.existsByMember_IdAndFriend_Id(friend.getId(), loginMember.getId()))
        .thenReturn(true);

    Assertions.assertThrows(BadRequestException.class,
        () -> friendInfoService.add(friend.getId()));
  }

  @Test
  @DisplayName("로그인 하지 않은 상태에서 친구추가")
  public void add_NotLogin() {
    when(memberService.getLoginMember()).thenThrow(BadRequestException.class);

    Assertions.assertThrows(BadRequestException.class,
        () -> friendInfoService.add(friend.getId()));
  }

  @Test
  @DisplayName("존재하지 않은 사용자를 친구추가")
  public void add_NotExistFriend() {
    when(memberService.getLoginMember()).thenReturn(loginMember);
    when(memberService.getMemberById(friend.getId())).thenThrow(BadRequestException.class);

    Assertions.assertThrows(BadRequestException.class,
        () -> friendInfoService.add(friend.getId()));
  }
}