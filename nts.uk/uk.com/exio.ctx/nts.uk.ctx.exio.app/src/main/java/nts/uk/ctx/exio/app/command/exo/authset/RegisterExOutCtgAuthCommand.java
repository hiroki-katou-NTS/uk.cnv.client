package nts.uk.ctx.exio.app.command.exo.authset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
public class RegisterExOutCtgAuthCommand {
    String roleId;

    List<FunctionAuthSetting> functionAuthSettings;
}

@AllArgsConstructor
@NoArgsConstructor
@Getter
class FunctionAuthSetting {
    private int functionNo;

    private boolean available;
}


