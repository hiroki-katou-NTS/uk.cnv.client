package nts.uk.ctx.exio.app.command.exo.authset;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class DuplicateExOutputCtgAuthResult {
    /** 結果（OK/NG) */
    boolean isSuccess;

    /** 複写先ロールID */
    String copyDestinationRoleId;

    /** 上書き（する/しない） */
    boolean isOverwrite;
}
