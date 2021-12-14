package nts.uk.ctx.at.schedule.pub.schedule.basicschedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.schedule.pub.schedule.workschedule.BreakTimeSheetExport;
import nts.uk.shr.com.time.TimeWithDayAttr;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ScWorkScheduleExport_New {
	// BasicSchedule
	
	// 社員ID
	private String employeeId;
	
	// 年月日
	private GeneralDate date;
	
	// 勤務種類コード
	private String workTypeCode;

	// 就業時間帯コード
	private Optional<String> workTimeCode = Optional.empty();
	
	// 開始時刻1
	private Optional<TimeWithDayAttr> scheduleStartClock1 = Optional.empty();
	
	// 終了時刻1
	private Optional<TimeWithDayAttr> scheduleEndClock1 = Optional.empty();
	
	// 開始時刻2
	private Optional<TimeWithDayAttr> scheduleStartClock2 = Optional.empty();
	
	// 終了時刻2
	private Optional<TimeWithDayAttr> scheduleEndClock2 = Optional.empty();
	
    // 育児時間
	private Integer childTime;
	
	// 短時間勤務時間帯
	private List<ShortWorkingTimeSheetExport> listShortWorkingTimeSheetExport = new ArrayList<>();
	
	// 休憩時間帯時間帯
	private List<BreakTimeSheetExport> listBreakTimeSheetExports = new ArrayList<BreakTimeSheetExport>();
		
}
