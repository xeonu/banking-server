package xeonu.bankingserver.alarm.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Primary
public class MockAlarmService implements AlarmService {

  @Override
  @KafkaListener(topics = "alarm", groupId = "alarm-group")
  public void sendAlarmMessage(int memberId, String message) {
    log.info("send {} to member {}", message, memberId);
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
