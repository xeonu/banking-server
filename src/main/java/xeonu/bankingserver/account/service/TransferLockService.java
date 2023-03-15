package xeonu.bankingserver.account.service;

import static xeonu.bankingserver.account.exception.AccountErrorResponse.TRANSFER_SERVER_ERROR;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import xeonu.bankingserver.common.exception.LockTimeOutException;

@Service
@RequiredArgsConstructor
public class TransferLockService {

  private final RedisTemplate<String, Object> redisTemplate;

  private final String lockKey = "transferLock";

  /**
   * redis에 lockKey에서 참조한 key로 "locked"라는 value를 추가합니다. 이 때 해당 key에 존재하는 값이 있다면 false를 반환합니다. key에
   * 존재하는 값이 없어서 새로운 (key, value)를 추가한다면 lock을 획득해서 true를 반환합니다.
   *
   * @return lock 획득 여부
   */
  public boolean lock() {
    Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked");
    return success != null && success;
  }

  /**
   * 획득한 lock을 해제합니다. redis에 생성한 (key, value)를 삭제하여 lock을 해제합니다.
   */
  public void unLock() {
    redisTemplate.delete(lockKey);
  }

  /**
   * maxRetry만큼 락획득을 시도합니다. 락 획득 간에 50ms의 시간텀을 두었고, maxRetry만큼 시도했는데 lock을 획득하지 못한다면
   * LockTimeOutException을 던집니다.
   *
   * @param maxRetry lock 획득 최대 시도횟수
   */
  public void lockLimitTry(int maxRetry) {
    int retry = 0;

    while (!lock()) {
      if (++retry == maxRetry) {
        throw new LockTimeOutException(TRANSFER_SERVER_ERROR.getErrorResponse());
      }

      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
