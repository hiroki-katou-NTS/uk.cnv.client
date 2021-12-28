package nts.uk.screen.at.app.kdw013.a;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.app.find.dailyperform.snapshot.SnapshotDto;
import nts.uk.ctx.at.record.app.find.dailyperform.workrecord.dto.OuenWorkTimeOfDailyAttendanceDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.screen.at.app.ksu001.displayinworkinformation.EditStateOfDailyAttdDto;

/**
 * 
 * @author tutt
 *
 */
@Setter
@Getter
public class IntegrationOfDailyDto {
	// 社員ID
	private String employeeId;

	// 年月日
	private GeneralDate ymd;

	// 勤務情報: 日別勤怠の勤務情報
	private WorkInfoOfDailyAttendanceDto workInformationDto;

	// 所属情報: 日別勤怠の所属情報

	private AffiliationInforOfDailyAttdDto affiliationInfor;

	// 計算区分: 日別勤怠の計算区分

	private CalAttrOfDailyAttdDto calAttr;

	// 日別勤怠の勤務種別（廃止）

	// 日別実績の出退勤 (old)
//		private Optional<TimeLeavingOfDailyPerformance> attendanceLeave;
	// 出退勤: 日別勤怠の出退勤

	//private TimeLeavingOfDailyAttdDto attendanceLeave;

	// 日別実績の休憩時間帯 (old)
//		private List<BreakTimeOfDailyPerformance> breakTime;
	// 休憩時間帯: 日別勤怠の休憩時間帯

	private BreakTimeOfDailyAttdDto breakTime;

	// 日別実績の外出時間帯 (old)
//		private Optional<OutingTimeOfDailyPerformance> outingTime;
	// 外出時間帯: 日別勤怠の外出時間帯

	//private OutingTimeOfDailyAttdDto outingTime;

	// 日別実績の短時間勤務時間帯 (old)
//		private Optional<ShortTimeOfDailyPerformance> shortTime;
	// 短時間勤務時間帯: 日別勤怠の短時間勤務時間帯

	//private ShortTimeOfDailyAttdDto shortTime;

	// 日別実績の臨時出退勤 (old)
//		private Optional<TemporaryTimeOfDailyPerformance> tempTime;
	// 臨時出退勤: 日別勤怠の臨時出退勤

	//private TemporaryTimeOfDailyAttdDto tempTime;

	// 日別実績の入退門 (old)
//		private Optional<AttendanceLeavingGateOfDaily> attendanceLeavingGate;
	// 入退門: 日別勤怠の入退門

	//private AttendanceLeavingGateOfDailyAttdDto attendanceLeavingGate;

	// 日別実績の勤怠時間 (old)
//		private Optional<AttendanceTimeOfDailyPerformance> attendanceTimeOfDailyPerformance;
	// 勤怠時間: 日別勤怠の勤怠時間

	//private AttendanceTimeOfDailyAttendanceDto attendanceTimeOfDailyPerformance;

	// 日別実績の特定日区分 (old)
//		private Optional<SpecificDateAttrOfDailyPerfor> specDateAttr;
	// 特定日区分: 日別勤怠の特定日区分
	//private SpecificDateAttrOfDailyAttdDto specDateAttr;

	// 社員の日別実績エラー一覧

//	private List<EmployeeDailyPerErrorDto> employeeError;
	// エラー一覧: 日別勤怠のエラー一覧 //phải chuyển sang shared ?

	// 日別実績の編集状態 (old)
//		private List<EditStateOfDailyPerformance> editState;
	// 編集状態: 日別勤怠の編集状態

	private List<EditStateOfDailyAttdDto> editState;

	// 日別実績の任意項目 (old)
//		private Optional<AnyItemValueOfDaily> anyItemValue;
	// 任意項目: 日別勤怠の任意項目

	//private AnyItemValueOfDailyAttdDto anyItemValue;

	//private PCLogOnInfoOfDailyAttdDto pcLogOnInfo;

	// 備考: 日別勤怠の備考
	private List<RemarksOfDailyAttdDto> remarks;
	/** 日別勤怠の応援作業時間 */
	private List<OuenWorkTimeOfDailyAttendanceDto> ouenTime = new ArrayList<>();

	/** 応援時間帯: 日別勤怠の応援作業時間帯 */
	private List<OuenWorkTimeSheetOfDailyAttendanceDto> ouenTimeSheet = new ArrayList<>();

	private SnapshotDto snapshot;

	public static IntegrationOfDailyDto toDto(IntegrationOfDaily domain) {
		IntegrationOfDailyDto result = new IntegrationOfDailyDto();

		result.setEmployeeId(domain.getEmployeeId());
		result.setYmd(domain.getYmd());
		result.setWorkInformationDto(WorkInfoOfDailyAttendanceDto.toDto(domain.getWorkInformation()));
		result.setAffiliationInfor(AffiliationInforOfDailyAttdDto.fromDomain(domain.getAffiliationInfor()));
		result.setCalAttr(CalAttrOfDailyAttdDto.fromDomain(domain.getCalAttr()));
//		result.setAttendanceLeave(TimeLeavingOfDailyAttdDto.fromDomain(domain.getAttendanceLeave()));
		result.setBreakTime(BreakTimeOfDailyAttdDto.fromDomain(domain.getBreakTime()));
//		result.setOutingTime(OutingTimeOfDailyAttdDto.fromDomain(domain.getOutingTime()));
//		result.setShortTime(ShortTimeOfDailyAttdDto.fromDomain(domain.getShortTime()));
//		result.setTempTime(TemporaryTimeOfDailyAttdDto.fromDomain(domain.getTempTime()));
//		result.setAttendanceLeavingGate(AttendanceLeavingGateOfDailyAttdDto.fromDomain(domain.getAttendanceLeavingGate()));
//		result.setAttendanceTimeOfDailyPerformance(AttendanceTimeOfDailyAttendanceDto.fromDomain(domain.getAttendanceTimeOfDailyPerformance()));
//		result.setSpecDateAttr(SpecificDateAttrOfDailyAttdDto.fromDomain(domain.getSpecDateAttr()));
//		result.setEmployeeError(domain.getEmployeeError().stream().map(x -> EmployeeDailyPerErrorDto.fromDomain(x)).collect(Collectors.toList()));
		result.setEditState(domain.getEditState().stream().map(x -> EditStateOfDailyAttdDto.fromDomain(x)).collect(Collectors.toList()));
//		result.setAnyItemValue(domain.getAnyItemValue().map(x -> AnyItemValueOfDailyAttdDto.fromDomain(x)).orElse(null));
//		result.setPcLogOnInfo(PCLogOnInfoOfDailyAttdDto.fromDomain(domain.getPcLogOnInfo()));
		result.setRemarks(domain.getRemarks().stream().map(x-> RemarksOfDailyAttdDto.fromDomain(x)).collect(Collectors.toList()));
		result.setOuenTime(domain.getOuenTime().stream().map(ot-> OuenWorkTimeOfDailyAttendanceDto.toDto(ot)).collect(Collectors.toList()));
		result.setOuenTimeSheet(domain.getOuenTimeSheet().stream().map(os-> OuenWorkTimeSheetOfDailyAttendanceDto.fromDomain(os)).collect(Collectors.toList()));
		result.setSnapshot(domain.getSnapshot().map(x -> SnapshotDto.from(domain.getEmployeeId(), domain.getYmd(), x))
				.orElse(null));
		return result;
	}
}
