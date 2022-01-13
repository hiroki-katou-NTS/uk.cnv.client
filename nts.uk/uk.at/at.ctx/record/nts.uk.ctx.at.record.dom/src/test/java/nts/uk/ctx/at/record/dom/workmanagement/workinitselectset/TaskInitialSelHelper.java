package nts.uk.ctx.at.record.dom.workmanagement.workinitselectset;

import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;

public class TaskInitialSelHelper {
	
	public static TaskInitialSel getTaskInitialSelDefault(){
		return new TaskInitialSel(
				"000000000001", 
				new DatePeriod( GeneralDate.ymd(2021, 06, 01),  GeneralDate.ymd(2021, 06, 30)), 
				new TaskItem(
						Optional.of(new TaskCode("TaskCode1") ),
						Optional.of(new TaskCode("TaskCode2") ),
						Optional.of(new TaskCode("TaskCode3") ),
						Optional.of(new TaskCode("TaskCode4") ),
						Optional.of(new TaskCode("TaskCode5") )
						));
	}
	
	public static TaskItem getParamSetTaskItem(){
		return new TaskItem(
						Optional.of(new TaskCode("TaskCode6") ),
						Optional.of(new TaskCode("TaskCode7") ),
						Optional.of(new TaskCode("TaskCode8") ),
						Optional.of(new TaskCode("TaskCode9") ),
						Optional.of(new TaskCode("TaskCode10") )
						);
	}

}
