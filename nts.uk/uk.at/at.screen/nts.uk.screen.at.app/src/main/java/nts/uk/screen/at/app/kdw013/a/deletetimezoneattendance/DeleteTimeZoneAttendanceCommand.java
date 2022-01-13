package nts.uk.screen.at.app.kdw013.a.deletetimezoneattendance;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDate;

/**
 * 
 * @author sonnlb
 *
 */
@AllArgsConstructor
@Getter
public class DeleteTimeZoneAttendanceCommand {
	/** 対象者 */
	private String employeeId;

	/** 変更対象日 */
	private List<GeneralDate> changedDates;
}
