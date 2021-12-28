package nts.uk.ctx.exio.app.command.exo.authset;

import lombok.Value;

@Value
public class DuplicateExOutCtgAuthCommand {
    /** 複写元ロールID */
    String sourceRoleId;

    /** 複写先ロールID */
    String destinationRoleId;

    /** 上書き */
    boolean overWrite;
}
