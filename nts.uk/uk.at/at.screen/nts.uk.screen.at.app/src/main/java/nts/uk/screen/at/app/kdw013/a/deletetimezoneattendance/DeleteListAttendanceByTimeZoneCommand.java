package nts.uk.screen.at.app.kdw013.a.deletetimezoneattendance;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDate;

/**
 * 
 * @author sonnlb
 */
@AllArgsConstructor
@Getter
public class DeleteListAttendanceByTimeZoneCommand {
	// 年月日
	private GeneralDate date;
	// 一覧
	private List<DeleteAttendanceByTimeZoneCommand> list;
}
