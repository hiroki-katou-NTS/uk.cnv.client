package nts.uk.ctx.sys.auth.app.command.cmm051;

import lombok.Data;
import nts.arc.time.GeneralDate;

@Data
public class RegisterWorkplaceManagerCommand {
    private String  wkpManagerId;
    private GeneralDate startDate;
    private GeneralDate endDate;
}
