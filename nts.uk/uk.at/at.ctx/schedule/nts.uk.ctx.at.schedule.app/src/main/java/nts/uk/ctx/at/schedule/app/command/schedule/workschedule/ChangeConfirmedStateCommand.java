package nts.uk.ctx.at.schedule.app.command.schedule.workschedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ChangeConfirmedStateCommand {

	public String sid;
	
	public GeneralDate ymd;
	
	boolean isConfirmed;
}
