package xeonu.bankingserver.account.utility;

import java.util.concurrent.ThreadLocalRandom;

public class AccountUtil {

  public static String randomNumberMaker(int length) {
    StringBuilder builder = new StringBuilder();

    for (int i = 0; i < length; i++) {
      builder.append(ThreadLocalRandom.current().nextInt(10));
    }

    return builder.toString();
  }
}
