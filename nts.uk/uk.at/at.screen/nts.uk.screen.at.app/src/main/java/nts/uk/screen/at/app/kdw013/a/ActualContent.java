package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkTimeInformation;

/**
 * 
 * @author tutt
 *
 */
@Setter
@Getter
public class ActualContent {
	
	//休憩リスト
	private List<BreakTimeSheet> breakTimeSheets;
	
	//休憩時間
	private Optional<AttendanceTime> breakHours;
	
	//終了時刻
	private Optional<WorkTimeInformation> end = Optional.empty();

	// 総労働時間
	private Optional<AttendanceTime> totalWorkingHours;

	// 開始時刻
	private Optional<WorkTimeInformation> start = Optional.empty();
	
}
