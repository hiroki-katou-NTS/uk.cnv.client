package nts.uk.ctx.at.record.dom.workmanagement.workinitselectset;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;

public class TaskInitialSelHistHelper {
	
	public static TaskInitialSelHist getTaskInitialSelHistDefault(){
		TaskInitialSel item1 = new TaskInitialSel("000000000001", new DatePeriod( GeneralDate.ymd(2021, 06, 01),  GeneralDate.ymd(2021, 06, 30)), 
				new TaskItem(
						Optional.of(new TaskCode("TaskCode1") ),
						Optional.of(new TaskCode("TaskCode2") ),
						Optional.of(new TaskCode("TaskCode3") ),
						Optional.of(new TaskCode("TaskCode4") ),
						Optional.of(new TaskCode("TaskCode5") )
						));
		TaskInitialSel item2 = new TaskInitialSel("000000000001", new DatePeriod( GeneralDate.ymd(2021, 07, 01),  GeneralDate.ymd(9999, 12, 31)), 
				new TaskItem(
						Optional.of(new TaskCode("TaskCode1") ),
						Optional.of(new TaskCode("TaskCode2") ),
						Optional.of(new TaskCode("TaskCode3") ),
						Optional.of(new TaskCode("TaskCode4") ),
						Optional.of(new TaskCode("TaskCode5") )
						));
		List<TaskInitialSel> lst = new ArrayList<>();
		lst.add(item1);
		lst.add(item2);
		TaskInitialSelHist taskInitialSelHist = new TaskInitialSelHist(
				"000000000001",
				lst);
		return taskInitialSelHist;
	}
	
	public static TaskInitialSel getParamTestaddHistory(){
		return  new TaskInitialSel("000000000001", new DatePeriod( GeneralDate.ymd(2021, 7,15),  GeneralDate.ymd(9999, 12,31)), 
				new TaskItem(
						Optional.of(new TaskCode("TaskCode1") ),
						Optional.of(new TaskCode("TaskCode2") ),
						Optional.of(new TaskCode("TaskCode3") ),
						Optional.of(new TaskCode("TaskCode4") ),
						Optional.of(new TaskCode("TaskCode5") )
						));
	}
	
	public static TaskInitialSel getParamTestdeleteHistory(){
		return  new TaskInitialSel("000000000001", new 
				DatePeriod( GeneralDate.ymd(2021, 07, 01),  GeneralDate.ymd(9999, 12, 31)), 
				new TaskItem(
						Optional.of(new TaskCode("TaskCode1") ),
						Optional.of(new TaskCode("TaskCode2") ),
						Optional.of(new TaskCode("TaskCode3") ),
						Optional.of(new TaskCode("TaskCode4") ),
						Optional.of(new TaskCode("TaskCode5") )
						));
	}
	
	public static TaskItem getParamTaskItemChangeHistory(){
		return  new TaskItem(
				Optional.of(new TaskCode("TaskCode6")),
				Optional.of(new TaskCode("TaskCode7")),
				Optional.of(new TaskCode("TaskCode8")),
				Optional.of(new TaskCode("TaskCode9")),
				Optional.of(new TaskCode("TaskCode10")));
	}
}
