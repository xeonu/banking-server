package xeonu.bankingserver.account.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferDto {

  @NotNull
  private int senderAccountId;

  @NotNull
  private int receiverAccountId;

  @NotNull
  @Min(1)
  private long amount;
}
