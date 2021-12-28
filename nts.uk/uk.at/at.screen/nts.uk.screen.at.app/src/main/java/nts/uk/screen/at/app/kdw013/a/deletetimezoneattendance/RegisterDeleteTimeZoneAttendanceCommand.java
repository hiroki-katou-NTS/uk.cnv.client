package nts.uk.screen.at.app.kdw013.a.deletetimezoneattendance;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourinput.DeleteAttendancesByTimezone;

/**
 * 
 * @author sonnlb
 * 時間帯別勤怠の削除一覧
 */
@AllArgsConstructor
@Getter
public class RegisterDeleteTimeZoneAttendanceCommand {
	// 社員ID
	private String employeeId;
	// 年月日リスト
	private List<DeleteListAttendanceByTimeZoneCommand> deleteList;

	public List<DeleteAttendancesByTimezone> toDomain() {

		return deleteList.stream()
				.map(x -> new DeleteAttendancesByTimezone(employeeId, x.getDate(),
						x.getList().stream().map(at -> at.toDomain()).collect(Collectors.toList())))
				.collect(Collectors.toList());
	}
}
