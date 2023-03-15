package xeonu.bankingserver.member.dto;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {

  @NotNull
  private String loginId;

  @NotNull
  private String password;
}
