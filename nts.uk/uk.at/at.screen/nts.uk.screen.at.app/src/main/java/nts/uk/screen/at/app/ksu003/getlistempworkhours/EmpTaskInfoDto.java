package nts.uk.screen.at.app.ksu003.getlistempworkhours;

import java.util.List;

import lombok.Value;
import nts.arc.time.GeneralDate;
/**
 * 
 * @author HieuLt
 *
 */

@Value
public class EmpTaskInfoDto {
	//年月日
	public GeneralDate date; 
	//社員ID
	public String empID;
	//  <<Optional>> OrderedList<作業予定詳細、作業>: OrderedList<作業予定詳細、作業>
	
	//作業予定詳細
	private List<TaskScheduleDetailDto> taskScheduleDetail;

	public EmpTaskInfoDto(GeneralDate date, String empID, List<TaskScheduleDetailDto> taskScheduleDetail) {
		super();
		this.date = date;
		this.empID = empID;
		this.taskScheduleDetail = taskScheduleDetail;
	}
	
}
