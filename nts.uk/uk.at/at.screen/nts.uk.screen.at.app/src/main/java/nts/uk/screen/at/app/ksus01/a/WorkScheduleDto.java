package nts.uk.screen.at.app.ksus01.a;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkScheduleDto {

	// 勤務予定.年月日
	private String ymd;
	
	// シフトマスタ
	private ShiftMasterDto shiftMaster;
	
	// 	出勤休日区分
	private Integer workAtr; 
	
	// 勤務予定.出退勤
	private List<AttendanceDto> listAttendaceDto;
}
