package nts.uk.screen.at.app.kdw013.e;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.TimeSheetOfAttendanceEachOuenSheet;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;

/**
 * 
 * @author tutt
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeSheetOfAttendanceEachOuenSheetCommand {

	/** 勤務枠No: 勤務NO */
	public Integer workNo;

	/** 開始: 勤務時刻情報 */
	public WorkTimeInformationCommand start;

	/** 終了: 勤務時刻情報 */
	public WorkTimeInformationCommand end;

	public TimeSheetOfAttendanceEachOuenSheet toDomain() {
		return TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(this.workNo),
				Optional.ofNullable(this.start == null ? null : WorkTimeInformationCommand.toDomain(this.start)),
				Optional.ofNullable(this.end == null ? null : WorkTimeInformationCommand.toDomain(this.end)));
	}

}
