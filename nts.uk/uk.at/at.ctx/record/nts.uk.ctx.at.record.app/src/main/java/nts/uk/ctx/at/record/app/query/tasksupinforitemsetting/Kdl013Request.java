package nts.uk.ctx.at.record.app.query.tasksupinforitemsetting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;

@AllArgsConstructor
@Setter
@Getter
public class Kdl013Request {
	private int atdId;
	private String baseDate;
	
	public GeneralDate toDate() {
		return GeneralDate.fromString(this.baseDate, "yyyy/MM/dd");
	}

}
