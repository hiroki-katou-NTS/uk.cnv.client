package nts.uk.ctx.at.schedule.app.find.task.taskschedule.test;

import java.util.List;

import lombok.Value;
import nts.arc.time.GeneralDate;

/**
 * for get data screen test ksu003c
 * @author quytb
 *
 */
@Value
public class Ksu003cTestRequest {
	
	private List<String> empIds;
	private String ymd;
	
	public GeneralDate toDate() {
		return GeneralDate.fromString(this.ymd, "yyyy/MM/dd");
	}

}
