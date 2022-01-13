package nts.uk.screen.at.app.kdw013.a;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.app.find.dailyperform.workrecord.dto.OuenWorkTimeOfDailyAttendanceDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

@Getter
@AllArgsConstructor
public class IntegrationOfDailyCommand {
	// 社員ID
	private String employeeId;

	// 年月日
	private String ymd;

	// 勤務情報: 日別勤怠の勤務情報
	private WorkInfoOfDailyAttendanceCommand workInformationDto;

	// 所属情報: 日別勤怠の所属情報

	private AffiliationInforOfDailyAttdCommand affiliationInfor;

	// 計算区分: 日別勤怠の計算区分

	private CalAttrOfDailyAttdCommand calAttr;

	// 日別勤怠の勤務種別（廃止）

	// 日別実績の出退勤 (old)
//		private Optional<TimeLeavingOfDailyPerformance> attendanceLeave;
	// 出退勤: 日別勤怠の出退勤

	//private TimeLeavingOfDailyAttdCommand attendanceLeave;

	// 日別実績の休憩時間帯 (old)
//		private List<BreakTimeOfDailyPerformance> breakTime;
	// 休憩時間帯: 日別勤怠の休憩時間帯


	private BreakTimeOfDailyAttdCommand breakTime;
	// 日別実績の外出時間帯 (old)
//		private Optional<OutingTimeOfDailyPerformance> outingTime;
	// 外出時間帯: 日別勤怠の外出時間帯

	//private OutingTimeOfDailyAttdCommand outingTime;

	// 日別実績の短時間勤務時間帯 (old)
//		private Optional<ShortTimeOfDailyPerformance> shortTime;
	// 短時間勤務時間帯: 日別勤怠の短時間勤務時間帯

	//private ShortTimeOfDailyAttdCommand shortTime;

	// 日別実績の臨時出退勤 (old)
//		private Optional<TemporaryTimeOfDailyPerformance> tempTime;
	// 臨時出退勤: 日別勤怠の臨時出退勤

	//private TemporaryTimeOfDailyAttdCommand tempTime;

	// 日別実績の入退門 (old)
//		private Optional<AttendanceLeavingGateOfDaily> attendanceLeavingGate;
	// 入退門: 日別勤怠の入退門

	//private AttendanceLeavingGateOfDailyAttdCommand attendanceLeavingGate;

	// 日別実績の勤怠時間 (old)
//		private Optional<AttendanceTimeOfDailyPerformance> attendanceTimeOfDailyPerformance;
	// 勤怠時間: 日別勤怠の勤怠時間

	//private AttendanceTimeOfDailyAttendanceCommand attendanceTimeOfDailyPerformance;

	// 日別実績の特定日区分 (old)
//		private Optional<SpecificDateAttrOfDailyPerfor> specDateAttr;
	// 特定日区分: 日別勤怠の特定日区分
	//private SpecificDateAttrOfDailyAttdDto specDateAttr;

	// 社員の日別実績エラー一覧

	//private List<EmployeeDailyPerErrorDto> employeeError;
	// エラー一覧: 日別勤怠のエラー一覧 //phải chuyển sang shared ?

	// 日別実績の編集状態 (old)
//		private List<EditStateOfDailyPerformance> editState;
	// 編集状態: 日別勤怠の編集状態

	private List<EditStateOfDailyAttdCommand> editState;

	// 日別実績の任意項目 (old)
//		private Optional<AnyItemValueOfDaily> anyItemValue;
	// 任意項目: 日別勤怠の任意項目

	//private AnyItemValueOfDailyAttdDto anyItemValue;

	//private PCLogOnInfoOfDailyAttdDto pcLogOnInfo;

	// 備考: 日別勤怠の備考
	private List<RemarksOfDailyAttdCommand> remarks;
	/** 日別勤怠の応援作業時間 */
	private List<OuenWorkTimeOfDailyAttendanceDto> ouenTime = new ArrayList<>();

	/** 応援時刻: 日別勤怠の応援作業時間帯 */
	private List<OuenWorkTimeSheetOfDailyAttendanceCommand> ouenTimeSheet = new ArrayList<>();

	//private SnapshotDto snapshot;

	public static IntegrationOfDaily toDomain(IntegrationOfDailyCommand id) {
		IntegrationOfDaily result = new IntegrationOfDaily();
		
		result.setEmployeeId(id.getEmployeeId());
		result.setYmd(GeneralDate.fromString(id.getYmd(), "yyyy/MM/dd") );
		result.setWorkInformation(id.getWorkInformationDto().toDomain());
		result.setAffiliationInfor(id.getAffiliationInfor().toDomain());
		result.setCalAttr(id.getCalAttr().toDomain());
		result.setAttendanceLeave(Optional.empty());
		result.setBreakTime(id.getBreakTime().toDomain());
		result.setOutingTime(Optional.empty());
		result.setShortTime(Optional.empty());
		result.setTempTime(Optional.empty());
		result.setAttendanceLeavingGate(Optional.empty());
		result.setAttendanceTimeOfDailyPerformance(Optional.empty());
		result.setEditState(id.getEditState().stream().map(x-> x.toDomain()).collect(Collectors.toList()));
		result.setRemarks(id.getRemarks().stream().map(x-> x.toDomain()).collect(Collectors.toList()));
		result.setOuenTime(id.getOuenTime().stream().map(x-> x.toDomain()).collect(Collectors.toList()));
		result.setOuenTimeSheet(id.getOuenTimeSheet().stream().map(x -> x.toDomain()).collect(Collectors.toList()));
		result.setSpecDateAttr(Optional.empty());
		result.setAnyItemValue(Optional.empty());
		result.setTempTime(Optional.empty());
		result.setPcLogOnInfo(Optional.empty());
		result.setSnapshot(null);
		return result;
	}
}
