package nts.uk.screen.at.app.kdw013.a;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.EmployeeDailyPerError;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class EmployeeDailyPerErrorDto {

	private String id;
	/** 会社ID: 会社ID */
	private String companyID;

	/** エラー発生社員: 社員ID */
	private String employeeID;

	/** 処理年月日: 年月日 */
	private GeneralDate date;

	/** エラー: 勤務実績のエラーアラームコード */
	private String errorAlarmWorkRecordCode;

	/** 項目一覧: 勤怠項目ID */
	
	private List<Integer> attendanceItemList;

	/** エラー解除する */
	private int errorCancelAble;

	/** エラーアラームメッセージ */
	private String errorAlarmMessage;

	public static EmployeeDailyPerErrorDto fromDomain(EmployeeDailyPerError domain) {
		return new EmployeeDailyPerErrorDto(domain.getId(),
				domain.getCompanyID(),
				domain.getEmployeeID(), 
				domain.getDate(), 
				domain.getErrorAlarmWorkRecordCode().v(),
				domain.getAttendanceItemList(), 
				domain.getErrorCancelAble(), 
				domain.getErrorAlarmMessage().map(x-> x.v()).orElse(null));
	}

}
