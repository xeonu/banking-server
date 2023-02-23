package xeonu.bankingserver.member.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpDto {

  @NotNull
  @Size(min = 8, max = 16, message = "아이디는 8~16자로만 가입가능합니다.")
  private String loginId;

  @NotNull
  @Size(min = 8, max = 16, message = "비밀번호는 8~16자로만 가입가능합니다.")
  @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 숫자, 영어, 특수문자가 최소 1회씩 포함되어야합니다.")
  private String password;
}
