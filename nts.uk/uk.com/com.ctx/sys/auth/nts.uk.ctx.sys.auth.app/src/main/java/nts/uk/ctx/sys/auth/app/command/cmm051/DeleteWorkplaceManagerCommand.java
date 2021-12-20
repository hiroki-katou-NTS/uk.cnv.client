package nts.uk.ctx.sys.auth.app.command.cmm051;

import lombok.Data;

@Data
public class DeleteWorkplaceManagerCommand {
   private String workplaceId;
   private String sid;
}
