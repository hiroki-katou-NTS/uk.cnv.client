package nts.uk.ctx.at.request.dom.application.common.adapter.schedule.schedule.basicschedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDate;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * refactor 4
 * @author Doan Duy Hung
 *
 */
@AllArgsConstructor
@Getter
public class ScBasicScheduleImport {
	// 社員ID
	private String employeeId;
	// 年月日
	private GeneralDate date;
	// 勤務種類コード
	private String workTypeCode;

	// 就業時間帯コード
	private Optional<String> workTimeCode;
	// 開始時刻1
	private Optional<TimeWithDayAttr> scheduleStartClock1;
	// 終了時刻1
	private Optional<TimeWithDayAttr> scheduleEndClock1;
	// 開始時刻2
	private Optional<TimeWithDayAttr> scheduleStartClock2;
	// 終了時刻2
	private Optional<TimeWithDayAttr> scheduleEndClock2;
	// 育児時間
	private Integer childTime;
	// 短時間勤務時間帯
	private List<ShortWorkingTimeSheetImport> listShortWorkingTimeSheetExport = new ArrayList<>();
	
	// 休憩時間帯時間帯
    private List<BreakTimeSheetImport> listBreakTimeSheetExports = new ArrayList<BreakTimeSheetImport>();

}
