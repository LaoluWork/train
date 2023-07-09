package req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MemberRegisterReq {

    @NotBlank(message = "【手机号】不能为空")
    String mobile;
}
