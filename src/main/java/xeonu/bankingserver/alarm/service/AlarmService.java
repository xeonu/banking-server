package xeonu.bankingserver.alarm.service;

public interface AlarmService {

  /**
   * 특정 member에게 일림메시지를 전송합니다.
   *
   * @param memberId 알림메시지를 수신하는 member의 id
   * @param message  알림메시지 내용
   */
  public void sendAlarmMessage(int memberId, String message);
}
