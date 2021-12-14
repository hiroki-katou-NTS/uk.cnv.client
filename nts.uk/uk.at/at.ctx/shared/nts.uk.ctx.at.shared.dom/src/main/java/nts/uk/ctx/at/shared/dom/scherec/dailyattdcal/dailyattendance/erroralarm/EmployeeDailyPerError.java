package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;

/**
 * 
 * @author nampt
 * 社員の日別実績エラー一覧
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared(勤務予定、勤務実績).日の勤怠計算.日別勤怠.エラーアラーム
 */
@Getter
@NoArgsConstructor
public class EmployeeDailyPerError extends AggregateRoot {
	private String id;
	/** 会社ID: 会社ID */
	private String companyID;
	
	/** エラー発生社員: 社員ID*/
	private String employeeID;
	
	/** 処理年月日: 年月日*/
	private GeneralDate date;
	
	/** エラー: 勤務実績のエラーアラームコード*/
	private ErrorAlarmWorkRecordCode errorAlarmWorkRecordCode;
	
	/** 項目一覧: 勤怠項目ID */
	@Setter
	private List<Integer> attendanceItemList;

	/** エラー解除する */
	private int errorCancelAble;
	
	/** エラーアラームメッセージ */
	private Optional<ErrorAlarmMessage> errorAlarmMessage;

	public EmployeeDailyPerError(String companyID, String employeeID, GeneralDate date,
			String errorAlarmWorkRecordCode, List<Integer> attendanceItemList, int errorCancelAble,
			String errorAlarmMessage) {
		super();
		this.companyID = companyID;
		this.employeeID = employeeID;
		this.date = date;
		this.errorAlarmWorkRecordCode = new ErrorAlarmWorkRecordCode(errorAlarmWorkRecordCode);
		this.attendanceItemList = attendanceItemList == null ? new ArrayList<>() : attendanceItemList;
		this.errorCancelAble = errorCancelAble;
		this.errorAlarmMessage = errorAlarmMessage == null ? Optional.empty() : Optional.of(new ErrorAlarmMessage(errorAlarmMessage));
	}
	
	public EmployeeDailyPerError(String companyID, String employeeID, GeneralDate date,
			ErrorAlarmWorkRecordCode errorAlarmWorkRecordCode, List<Integer> attendanceItemList, int errorCancelAble,
			String errorAlarmMessage) {
		super();
		this.companyID = companyID;
		this.employeeID = employeeID;
		this.date = date;
		this.errorAlarmWorkRecordCode = errorAlarmWorkRecordCode;
		this.attendanceItemList = attendanceItemList == null ? new ArrayList<>() : attendanceItemList;
		this.errorCancelAble = errorCancelAble;
		this.errorAlarmMessage = errorAlarmMessage == null ? Optional.empty() : Optional.of(new ErrorAlarmMessage(errorAlarmMessage));
	}
	
	public EmployeeDailyPerError(String id, String companyID, String employeeID, GeneralDate date,
			ErrorAlarmWorkRecordCode errorAlarmWorkRecordCode, List<Integer> attendanceItemList, int errorCancelAble,
			String errorAlarmMessage) {
		super();
		this.id = id;
		this.companyID = companyID;
		this.employeeID = employeeID;
		this.date = date;
		this.errorAlarmWorkRecordCode = errorAlarmWorkRecordCode;
		this.attendanceItemList = attendanceItemList == null ? new ArrayList<>() : attendanceItemList;
		this.errorCancelAble = errorCancelAble;
		this.errorAlarmMessage = errorAlarmMessage == null ? Optional.empty() : Optional.of(new ErrorAlarmMessage(errorAlarmMessage));
	}

	public EmployeeDailyPerError(String companyID, String employeeID, GeneralDate date,
			ErrorAlarmWorkRecordCode errorAlarmWorkRecordCode, List<Integer> attendanceItemList, int errorCancelAble) {
		super();
		this.companyID = companyID;
		this.employeeID = employeeID;
		this.date = date;
		this.errorAlarmWorkRecordCode = errorAlarmWorkRecordCode;
		this.attendanceItemList = attendanceItemList == null ? new ArrayList<>() : attendanceItemList;;
		this.errorCancelAble = errorCancelAble;
		this.errorAlarmMessage = Optional.empty();
	}

	public EmployeeDailyPerError(String companyID, String employeeID, GeneralDate date,
			ErrorAlarmWorkRecordCode errorAlarmWorkRecordCode, List<Integer> attendanceItemList) {
		super();
		this.companyID = companyID;
		this.employeeID = employeeID;
		this.date = date;
		this.errorAlarmWorkRecordCode = errorAlarmWorkRecordCode;
		this.attendanceItemList = attendanceItemList == null ? new ArrayList<>() : attendanceItemList;
		this.errorCancelAble = 0;
		this.errorAlarmMessage = Optional.empty();
	}

	public EmployeeDailyPerError(String companyID, String employeeID, GeneralDate date,
			ErrorAlarmWorkRecordCode errorAlarmWorkRecordCode, Integer attendanceItemID) {
		super();
		this.companyID = companyID;
		this.employeeID = employeeID;
		this.date = date;
		this.errorAlarmWorkRecordCode = errorAlarmWorkRecordCode;
		List<Integer> attendanceItemList = new ArrayList<>();
		attendanceItemList.add(attendanceItemID);
		this.attendanceItemList = attendanceItemList;
		this.errorAlarmMessage = Optional.empty();
	}
	
	
}
