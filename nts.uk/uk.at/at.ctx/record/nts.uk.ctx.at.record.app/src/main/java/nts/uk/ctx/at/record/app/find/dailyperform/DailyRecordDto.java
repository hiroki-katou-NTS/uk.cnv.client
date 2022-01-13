package nts.uk.ctx.at.record.app.find.dailyperform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.app.find.dailyattendance.timesheet.ouen.dto.OuenWorkTimeSheetOfDailyDto;
import nts.uk.ctx.at.record.app.find.dailyperform.affiliationInfor.dto.AffiliationInforOfDailyPerforDto;
import nts.uk.ctx.at.record.app.find.dailyperform.attendanceleavinggate.dto.AttendanceLeavingGateOfDailyDto;
import nts.uk.ctx.at.record.app.find.dailyperform.calculationattribute.dto.CalcAttrOfDailyPerformanceDto;
import nts.uk.ctx.at.record.app.find.dailyperform.customjson.CustomGeneralDateSerializer;
import nts.uk.ctx.at.record.app.find.dailyperform.customjson.CustomOptionalDeserializer;
import nts.uk.ctx.at.record.app.find.dailyperform.customjson.CustomOptionalSerializer;
import nts.uk.ctx.at.record.app.find.dailyperform.dto.AttendanceTimeDailyPerformDto;
import nts.uk.ctx.at.record.app.find.dailyperform.editstate.EditStateOfDailyPerformanceDto;
import nts.uk.ctx.at.record.app.find.dailyperform.erroralarm.dto.EmployeeDailyPerErrorDto;
import nts.uk.ctx.at.record.app.find.dailyperform.goout.dto.OutingTimeOfDailyPerformanceDto;
import nts.uk.ctx.at.record.app.find.dailyperform.optionalitem.dto.OptionalItemOfDailyPerformDto;
import nts.uk.ctx.at.record.app.find.dailyperform.pclogoninfor.dto.PCLogOnInforOfDailyPerformDto;
import nts.uk.ctx.at.record.app.find.dailyperform.remark.dto.RemarksOfDailyDto;
import nts.uk.ctx.at.record.app.find.dailyperform.resttime.dto.BreakTimeDailyDto;
import nts.uk.ctx.at.record.app.find.dailyperform.shorttimework.dto.ShortTimeOfDailyDto;
import nts.uk.ctx.at.record.app.find.dailyperform.snapshot.SnapshotDto;
import nts.uk.ctx.at.record.app.find.dailyperform.specificdatetttr.dto.SpecificDateAttrOfDailyPerforDto;
import nts.uk.ctx.at.record.app.find.dailyperform.temporarytime.dto.TemporaryTimeOfDailyPerformanceDto;
import nts.uk.ctx.at.record.app.find.dailyperform.workinfo.dto.WorkInformationOfDailyDto;
import nts.uk.ctx.at.record.app.find.dailyperform.workrecord.dto.AttendanceTimeByWorkOfDailyDto;
import nts.uk.ctx.at.record.app.find.dailyperform.workrecord.dto.OuenWorkTimeOfDailyDto;
import nts.uk.ctx.at.record.app.find.dailyperform.workrecord.dto.TimeLeavingOfDailyPerformanceDto;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeSheetOfDaily;
import nts.uk.ctx.at.shared.dom.attendance.util.item.AttendanceItemDataGate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemRoot;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.AttendanceItemCommon;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItem;

@Data
@EqualsAndHashCode(callSuper = false)
/** 日別実績（WORK） */
@AttendanceItemRoot(isContainer = true)
public class DailyRecordDto extends AttendanceItemCommon {

	/***/
	private static final long serialVersionUID = 1L;
	
	private String employeeId;

	@JsonDeserialize(using = CustomGeneralDateSerializer.class)
	private GeneralDate date;

	/** 勤務情報： 日別実績の勤務情報 */
	@AttendanceItemLayout(layout = DAILY_WORK_INFO_CODE, jpPropertyName = DAILY_WORK_INFO_NAME)
	private WorkInformationOfDailyDto workInfo;

	/** 計算区分： 日別実績の計算区分 */
	@AttendanceItemLayout(layout = DAILY_CALCULATION_ATTR_CODE, jpPropertyName = DAILY_CALCULATION_ATTR_NAME)
	private CalcAttrOfDailyPerformanceDto calcAttr;

	/** 所属情報： 日別実績の所属情報 */
	@AttendanceItemLayout(layout = DAILY_AFFILIATION_INFO_CODE, jpPropertyName = DAILY_AFFILIATION_INFO_NAME)
	private AffiliationInforOfDailyPerforDto affiliationInfo;

	/** エラー一覧： 社員の日別実績エラー一覧 */
	// TODO: list?
	// @AttendanceItemLayout(layout = "D", jpPropertyName = "社員の日別実績エラー一覧")
	private List<EmployeeDailyPerErrorDto> errors = new ArrayList<>();

	/** 外出時間帯: 日別実績の外出時間帯 */
	@AttendanceItemLayout(layout = DAILY_OUTING_TIME_CODE, jpPropertyName = DAILY_OUTING_TIME_NAME, isOptional = true)
	private Optional<OutingTimeOfDailyPerformanceDto> outingTime = Optional.empty();

	/** 休憩時間帯: 日別実績の休憩時間帯 */
	@AttendanceItemLayout(layout = DAILY_BREAK_TIME_CODE, jpPropertyName = DAILY_BREAK_TIME_NAME, isOptional = true)
	@JsonDeserialize(using = CustomOptionalDeserializer.class)
	@JsonSerialize(using = CustomOptionalSerializer.class)
	private Optional<BreakTimeDailyDto> breakTime = Optional.empty();

	/** 勤怠時間: 日別実績の勤怠時間 */
	@AttendanceItemLayout(layout = DAILY_ATTENDANCE_TIME_CODE, jpPropertyName = DAILY_ATTENDANCE_TIME_NAME, isOptional = true)
	@JsonDeserialize(using = CustomOptionalDeserializer.class)
	@JsonSerialize(using = CustomOptionalSerializer.class)
	private Optional<AttendanceTimeDailyPerformDto> attendanceTime = Optional.empty();

	/** 作業別勤怠時間: 日別実績の作業別勤怠時間 */
	@AttendanceItemLayout(layout = DAILY_ATTENDANCE_TIME_BY_WORK_CODE, jpPropertyName = DAILY_ATTENDANCE_TIME_BY_WORK_NAME, isOptional = true)
	@JsonDeserialize(using = CustomOptionalDeserializer.class)
	@JsonSerialize(using = CustomOptionalSerializer.class)
	private Optional<AttendanceTimeByWorkOfDailyDto> attendanceTimeByWork = Optional.empty();

	/** 出退勤: 日別実績の出退勤 */
	@AttendanceItemLayout(layout = DAILY_ATTENDACE_LEAVE_CODE, jpPropertyName = DAILY_ATTENDACE_LEAVE_NAME, isOptional = true)
	@JsonDeserialize(using = CustomOptionalDeserializer.class)
	@JsonSerialize(using = CustomOptionalSerializer.class)
	private Optional<TimeLeavingOfDailyPerformanceDto> timeLeaving = Optional.empty();

	/** 短時間勤務時間帯: 日別実績の短時間勤務時間帯 */
	@AttendanceItemLayout(layout = DAILY_SHORT_TIME_CODE, jpPropertyName = DAILY_SHORT_TIME_NAME, isOptional = true)
	@JsonDeserialize(using = CustomOptionalDeserializer.class)
	@JsonSerialize(using = CustomOptionalSerializer.class)
	private Optional<ShortTimeOfDailyDto> shortWorkTime = Optional.empty();

	/** 特定日区分: 日別実績の特定日区分 */
	@AttendanceItemLayout(layout = DAILY_SPECIFIC_DATE_ATTR_CODE, jpPropertyName = DAILY_SPECIFIC_DATE_ATTR_NAME, isOptional = true)
	@JsonDeserialize(using = CustomOptionalDeserializer.class)
	@JsonSerialize(using = CustomOptionalSerializer.class)
	private Optional<SpecificDateAttrOfDailyPerforDto> specificDateAttr = Optional.empty();

	/** 入退門: 日別実績の入退門 */
	@AttendanceItemLayout(layout = DAILY_ATTENDANCE_LEAVE_GATE_CODE, jpPropertyName = DAILY_ATTENDANCE_LEAVE_GATE_NAME, isOptional = true)
	@JsonDeserialize(using = CustomOptionalDeserializer.class)
	@JsonSerialize(using = CustomOptionalSerializer.class)
	private Optional<AttendanceLeavingGateOfDailyDto> attendanceLeavingGate = Optional.empty();

	/** 任意項目: 日別実績の任意項目 */
	@AttendanceItemLayout(layout = DAILY_OPTIONAL_ITEM_CODE, jpPropertyName = DAILY_OPTIONAL_ITEM_NAME, isOptional = true)
	@JsonDeserialize(using = CustomOptionalDeserializer.class)
	@JsonSerialize(using = CustomOptionalSerializer.class)
	private Optional<OptionalItemOfDailyPerformDto> optionalItem = Optional.empty();

	/** 編集状態: 日別実績の編集状態 */
	// @AttendanceItemLayout(layout = "N", jpPropertyName = "日別実績の編集状態", isList =
	// true, listMaxLength = ?)
	private List<EditStateOfDailyPerformanceDto> editStates = new ArrayList<>();

	/** 臨時出退勤: 日別実績の臨時出退勤 */
	@AttendanceItemLayout(layout = DAILY_TEMPORARY_TIME_CODE, jpPropertyName = DAILY_TEMPORARY_TIME_NAME, isOptional = true)
	@JsonDeserialize(using = CustomOptionalDeserializer.class)
	@JsonSerialize(using = CustomOptionalSerializer.class)
	private Optional<TemporaryTimeOfDailyPerformanceDto> temporaryTime = Optional.empty();
	
	/** PCログオン情報: 日別実績のPCログオン情報 */
	@AttendanceItemLayout(layout = DAILY_PC_LOG_INFO_CODE, jpPropertyName = DAILY_PC_LOG_INFO_NAME, isOptional = true)
	@JsonDeserialize(using = CustomOptionalDeserializer.class)
	@JsonSerialize(using = CustomOptionalSerializer.class)
	private Optional<PCLogOnInforOfDailyPerformDto> pcLogInfo = Optional.empty();
	
	/** 備考: 日別実績の備考 */
	@AttendanceItemLayout(layout = DAILY_REMARKS_CODE, jpPropertyName = DAILY_REMARKS_NAME)
	private RemarksOfDailyDto remarks;
	
	/** 日別実績の応援作業別勤怠時間 */
	@AttendanceItemLayout(layout = DAILY_SUPPORT_TIME_CODE, jpPropertyName = DAILY_SUPPORT_TIME_NAME, isOptional = true)
	@JsonDeserialize(using = CustomOptionalDeserializer.class)
	@JsonSerialize(using = CustomOptionalSerializer.class)
	private Optional<OuenWorkTimeOfDailyDto> ouenWorkTime = Optional.empty();

	/** 臨時出退勤: 日別実績の臨時出退勤 */
	@AttendanceItemLayout(layout = DAILY_SNAPSHOT_CODE, jpPropertyName = DAILY_SNAPSHOT_NAME, isOptional = true)
	@JsonDeserialize(using = CustomOptionalDeserializer.class)
	@JsonSerialize(using = CustomOptionalSerializer.class)
	private Optional<SnapshotDto> snapshot = Optional.empty();
	
	/**応援時刻: 日別勤怠の応援作業時間帯 */
	@AttendanceItemLayout( layout = DAILY_SUPPORT_TIMESHEET_CODE, jpPropertyName = DAILY_SUPPORT_TIMESHEET_NAME)
	private OuenWorkTimeSheetOfDailyDto ouenTimeSheet;

	
	public static DailyRecordDto from(IntegrationOfDaily domain){
		return from(domain, new HashMap<>());
	}
	
	public static DailyRecordDto from(IntegrationOfDaily domain, Map<Integer, OptionalItem> master){
		DailyRecordDto dto = new DailyRecordDto();
		if(domain != null){
			String employeeId = domain.getEmployeeId();
			GeneralDate ymd = domain.getYmd();
			dto.setEmployeeId(employeeId);
			dto.setDate(ymd);
			dto.setWorkInfo(WorkInformationOfDailyDto.getDto(employeeId,ymd,domain.getWorkInformation()));
			dto.setCalcAttr(CalcAttrOfDailyPerformanceDto.getDto(employeeId,ymd,domain.getCalAttr()));
			dto.setAffiliationInfo(AffiliationInforOfDailyPerforDto.getDto(employeeId,ymd,domain.getAffiliationInfor()));
//			dto.setBusinessType(domain.getBusinessType().map(b -> BusinessTypeOfDailyPerforDto.getDto(b)));
			if(domain.getEmployeeError() != null && !domain.getEmployeeError().isEmpty()) {
				dto.setErrors(domain.getEmployeeError().stream().map(x -> EmployeeDailyPerErrorDto.getDto(x)).collect(Collectors.toList()));
			}
			dto.setOutingTime(domain.getOutingTime().map(o -> OutingTimeOfDailyPerformanceDto.getDto(employeeId,ymd,o)));
			dto.setBreakTime(Optional.of(BreakTimeDailyDto.getDto(employeeId,ymd, domain.getBreakTime())));
			dto.setAttendanceTime(domain.getAttendanceTimeOfDailyPerformance().map(a -> AttendanceTimeDailyPerformDto.getDto(employeeId,ymd,a)));
//			dto.setAttendanceTimeByWork(domain.getAttendancetimeByWork().map(a -> AttendanceTimeByWorkOfDailyDto.getDto(a)));
			dto.setTimeLeaving(domain.getAttendanceLeave().map(a -> TimeLeavingOfDailyPerformanceDto.getDto(employeeId,ymd,a)));
			dto.setShortWorkTime(domain.getShortTime().map(s -> ShortTimeOfDailyDto.getDto(employeeId,ymd,s)));
			dto.setSpecificDateAttr(domain.getSpecDateAttr().map(s -> SpecificDateAttrOfDailyPerforDto.getDto(employeeId,ymd,s)));
			dto.setAttendanceLeavingGate(domain.getAttendanceLeavingGate().map(a -> AttendanceLeavingGateOfDailyDto.getDto(employeeId,ymd,a)));
			dto.setOptionalItem(domain.getAnyItemValue().map(a -> OptionalItemOfDailyPerformDto.getDto(employeeId,ymd,a,master)));
			dto.setEditStates(domain.getEditState().stream().map(c -> EditStateOfDailyPerformanceDto.getDto(employeeId,ymd,c)).collect(Collectors.toList()));
			dto.setTemporaryTime(domain.getTempTime().map(t -> TemporaryTimeOfDailyPerformanceDto.getDto(employeeId,ymd,t)));
			dto.setPcLogInfo(domain.getPcLogOnInfo().map(pc -> PCLogOnInforOfDailyPerformDto.from(employeeId,ymd,pc)));
			dto.setRemarks(RemarksOfDailyDto.getDto(employeeId,ymd,domain.getRemarks()));
			dto.setOuenWorkTime(domain.getOuenTime().isEmpty() ? Optional.empty() : Optional.of(OuenWorkTimeOfDailyDto.from(employeeId, ymd, domain.getOuenTime())));
			dto.setSnapshot(domain.getSnapshot().map(c -> SnapshotDto.from(employeeId, ymd, c)));
			OuenWorkTimeSheetOfDaily ouenSheetOfDaily = OuenWorkTimeSheetOfDaily.create(employeeId, ymd, domain.getOuenTimeSheet()); 
			dto.setOuenTimeSheet(OuenWorkTimeSheetOfDailyDto.getDto(employeeId, ymd, ouenSheetOfDaily));
			dto.exsistData();
		}
		return dto;
	}
	
	public static DailyRecordDto builder() {
		return new DailyRecordDto();
	}

	public DailyRecordDto withWorkInfo(WorkInformationOfDailyDto workInfo) {
		this.workInfo = workInfo;
		return this;
	}

	public DailyRecordDto withSnapshot(SnapshotDto snapshot) {
		this.snapshot = Optional.ofNullable(snapshot);
		return this;
	}
	
	public DailyRecordDto withCalcAttr(CalcAttrOfDailyPerformanceDto calcAttr) {
		this.calcAttr = calcAttr;
		return this;
	}

	public DailyRecordDto withAffiliationInfo(AffiliationInforOfDailyPerforDto affiliationInfo) {
		this.affiliationInfo = affiliationInfo;
		return this;
	}
	
	public DailyRecordDto withErrors(List<EmployeeDailyPerErrorDto> errors) {
		this.errors = errors;
		return this;
	}
	
	public DailyRecordDto withOuenSheet(OuenWorkTimeSheetOfDailyDto ouenSheet) {
		this.ouenTimeSheet = ouenSheet;
		return this;
	}
	
	public DailyRecordDto outingTime(OutingTimeOfDailyPerformanceDto outingTime) {
		this.outingTime = Optional.ofNullable(outingTime);
		return this;
	}
	
	public DailyRecordDto outingTimeO(Optional<OutingTimeOfDailyPerformanceDto> outingTime) {
		this.outingTime = outingTime;
		return this;
	}

	public DailyRecordDto breakTime(BreakTimeDailyDto breakTime) {
		this.breakTime = Optional.ofNullable(breakTime);
		return this;
	}

	public DailyRecordDto breakTimeO(Optional<BreakTimeDailyDto> breakTime) {
		this.breakTime = breakTime;
		return this;
	}

	public DailyRecordDto attendanceTime(AttendanceTimeDailyPerformDto attendanceTime) {
		this.attendanceTime = Optional.ofNullable(attendanceTime);
		return this;
	}

	public DailyRecordDto attendanceTimeO(Optional<AttendanceTimeDailyPerformDto> attendanceTime) {
		this.attendanceTime = attendanceTime;
		return this;
	}

	public DailyRecordDto attendanceTimeByWork(AttendanceTimeByWorkOfDailyDto attendanceTimeByWork) {
		this.attendanceTimeByWork = Optional.ofNullable(attendanceTimeByWork);
		return this;
	}

	public DailyRecordDto attendanceTimeByWorkO(Optional<AttendanceTimeByWorkOfDailyDto> attendanceTimeByWork) {
		this.attendanceTimeByWork = attendanceTimeByWork;
		return this;
	}

	public DailyRecordDto timeLeaving(TimeLeavingOfDailyPerformanceDto timeLeaving) {
		this.timeLeaving = Optional.ofNullable(timeLeaving);
		return this;
	}

	public DailyRecordDto timeLeavingO(Optional<TimeLeavingOfDailyPerformanceDto> timeLeaving) {
		this.timeLeaving = timeLeaving;
		return this;
	}

	public DailyRecordDto shortWorkTime(ShortTimeOfDailyDto shortWorkTime) {
		this.shortWorkTime = Optional.ofNullable(shortWorkTime);
		return this;
	}

	public DailyRecordDto shortWorkTimeO(Optional<ShortTimeOfDailyDto> shortWorkTime) {
		this.shortWorkTime = shortWorkTime;
		return this;
	}

	public DailyRecordDto specificDateAttr(SpecificDateAttrOfDailyPerforDto specificDateAttr) {
		this.specificDateAttr = Optional.ofNullable(specificDateAttr);
		return this;
	}

	public DailyRecordDto specificDateAttrO(Optional<SpecificDateAttrOfDailyPerforDto> specificDateAttr) {
		this.specificDateAttr = specificDateAttr;
		return this;
	}

	public DailyRecordDto attendanceLeavingGate(AttendanceLeavingGateOfDailyDto attendanceLeavingGate) {
		this.attendanceLeavingGate = Optional.ofNullable(attendanceLeavingGate);
		return this;
	}

	public DailyRecordDto attendanceLeavingGateO(Optional<AttendanceLeavingGateOfDailyDto> attendanceLeavingGate) {
		this.attendanceLeavingGate = attendanceLeavingGate;
		return this;
	}

	public DailyRecordDto optionalItems(OptionalItemOfDailyPerformDto optionalItem) {
		this.optionalItem = Optional.ofNullable(optionalItem);
		return this;
	}

	public DailyRecordDto optionalItemsO(Optional<OptionalItemOfDailyPerformDto> optionalItem) {
		this.optionalItem = optionalItem;
		return this;
	}

	public DailyRecordDto addEditStates(EditStateOfDailyPerformanceDto editStates) {
		if(this.editStates.stream().filter(c -> c.getAttendanceItemId() == editStates.getAttendanceItemId()).findFirst().isPresent()){
			return this;
		}
		this.editStates.add(editStates);
		return this;
	}

	public DailyRecordDto addEditStates(List<EditStateOfDailyPerformanceDto> editStates) {
		if (editStates == null) {
			return this;
		}
		List<Integer> current = this.editStates.stream().map(c -> c.getAttendanceItemId()).collect(Collectors.toList());
		this.editStates.addAll(editStates.stream().filter(c -> !current.contains(c.getAttendanceItemId())).collect(Collectors.toList()));
		return this;
	}

	public DailyRecordDto editStates(List<EditStateOfDailyPerformanceDto> editStates) {
		this.editStates = editStates == null ? new ArrayList<>() : editStates;
		return this;
	}

	public DailyRecordDto temporaryTime(TemporaryTimeOfDailyPerformanceDto temporaryTime) {
		this.temporaryTime = Optional.ofNullable(temporaryTime);
		return this;
	}

	public DailyRecordDto temporaryTimeO(Optional<TemporaryTimeOfDailyPerformanceDto> temporaryTime) {
		this.temporaryTime = temporaryTime;
		return this;
	}
	
	public DailyRecordDto pcLogInfo(PCLogOnInforOfDailyPerformDto pcLogInfo) {
		this.pcLogInfo = Optional.ofNullable(pcLogInfo);
		return this;
	}
	
	public DailyRecordDto pcLogInfoO(Optional<PCLogOnInforOfDailyPerformDto> pcLogInfo) {
		this.pcLogInfo = pcLogInfo;
		return this;
	}

	public DailyRecordDto remarks(RemarksOfDailyDto remarks) {
		this.remarks = remarks;
		return this;
	}
	
	public DailyRecordDto ouenTimeSheet(OuenWorkTimeSheetOfDailyDto ouenTimeSheet) {
		this.ouenTimeSheet = ouenTimeSheet;
		return this;
	}

	public DailyRecordDto withOuenWorkTime(OuenWorkTimeOfDailyDto ouenWorkTime) {
		this.ouenWorkTime = Optional.ofNullable(ouenWorkTime);
		return this;
	}
	
	public DailyRecordDto withOuenWorkTime(Optional<OuenWorkTimeOfDailyDto> ouenWorkTime) {
		this.ouenWorkTime = ouenWorkTime;
		return this;
	}
	
	public DailyRecordDto workingDate(GeneralDate workingDate) {
		this.date = workingDate;
		return this;
	}

	public DailyRecordDto employeeId(String employeeId) {
		this.employeeId = employeeId;
		return this;
	}

	public DailyRecordDto complete() {
		return this;
	}

	@Override
	public String employeeId() {
		return this.employeeId;
	}

	@Override
	public GeneralDate workingDate() {
		return this.date;  
	}

	@Override
	public IntegrationOfDaily toDomain(String employeeId, GeneralDate date) {
		IntegrationOfDaily integrationOfDaily = new IntegrationOfDaily(
				employeeId,
				date,
				this.workInfo == null ? null : this.workInfo.toDomain(employeeId, date),
				this.calcAttr == null ? null : this.calcAttr.toDomain(employeeId, date),
				this.affiliationInfo == null ? null : this.affiliationInfo.toDomain(employeeId, date),
				this.pcLogInfo.map(pc -> pc.toDomain(employeeId, date)),
				this.errors == null ? new ArrayList<>() : this.errors.stream().map(x -> x.toDomain(employeeId, date)).collect(Collectors.toList()),
				this.outingTime.map(ot -> ot.toDomain(employeeId, date)),
				this.breakTime.map(bt -> bt.toDomain(employeeId, date)).orElse(new BreakTimeOfDailyAttd()),
				this.attendanceTime.map(at -> {
					at.correctWithEditState(this.editStates);
					return at.toDomain(employeeId, date); 
				}),
				this.timeLeaving.map(tl -> tl.toDomain(employeeId, date)),
				this.shortWorkTime.map(swt -> swt.toDomain(employeeId, date)),
				this.specificDateAttr.map(sda -> sda.toDomain(employeeId, date)),
				this.attendanceLeavingGate.map(alg -> alg.toDomain(employeeId, date)),
				this.optionalItem.map(oi -> oi.toDomain(employeeId, date)),
				this.editStates.stream().map(editS -> editS.toDomain(employeeId, date)).collect(Collectors.toList()),
				this.temporaryTime.map(tt -> tt.toDomain(employeeId, date)),
				this.remarks == null ? new ArrayList<>() : this.remarks.toDomain(employeeId, date),
				this.ouenWorkTime.isPresent() ? this.ouenWorkTime.get().toDomain(employeeId, date).getOuenTimes() : new ArrayList<>(),
				new ArrayList<>(),
				this.snapshot.map(c -> c.toDomain(employeeId, date))
				);
		// set support time
		integrationOfDaily.setOuenTimeSheet(this.ouenTimeSheet == null ? new ArrayList<>() : this.ouenTimeSheet.toDomain(employeeId, date));
		
		return integrationOfDaily;
	}

//	@Override
//	public DailyRecordDto clone(){
//		IntegrationOfDaily integrationOfDaily = this.toDomain(employeeId, date);
//		return DailyRecordDto.from(integrationOfDaily).employeeId(employeeId).workingDate(date);
//	}
	
	@Override
	public DailyRecordDto clone(){
		DailyRecordDto dto = new DailyRecordDto();
		dto.setEmployeeId(employeeId);
		dto.setDate(date);
		dto.setWorkInfo(workInfo == null ? null : workInfo.clone());
		dto.setCalcAttr(calcAttr == null ? null : calcAttr.clone());
		dto.setAffiliationInfo(affiliationInfo == null ? null : affiliationInfo.clone());
		dto.setErrors(errors == null ? null : errors.stream().map(x -> x.clone()).collect(Collectors.toList()));
		dto.setOutingTime(outingTime.map(o -> o.clone()));
		dto.setBreakTime(breakTime.map(b -> b.clone()));
		dto.setAttendanceTime(attendanceTime.map(a -> a.clone()));
		dto.setAttendanceTimeByWork(attendanceTimeByWork.map(a -> a.clone()));
		dto.setTimeLeaving(timeLeaving.map(a -> a.clone()));
		dto.setShortWorkTime(shortWorkTime.map(s -> s.clone()));
		dto.setSpecificDateAttr(specificDateAttr.map(s -> s.clone()));
		dto.setAttendanceLeavingGate(attendanceLeavingGate.map(a -> a.clone()));
		dto.setOptionalItem(optionalItem.map(a -> a.clone()));
		dto.setEditStates(editStates.stream().map(c -> c.clone()).collect(Collectors.toList()));
		dto.setTemporaryTime(temporaryTime.map(t -> t.clone()));
		dto.setPcLogInfo(pcLogInfo.map(pc -> pc.clone()));
		dto.setRemarks(remarks == null ? null : remarks.clone());
		dto.setOuenWorkTime(ouenWorkTime.map(owt -> owt.clone()));
		dto.setSnapshot(snapshot.map(ss -> ss.clone()));
		dto.setOuenTimeSheet(ouenTimeSheet == null ? null : ouenTimeSheet.clone());
		if(isHaveData()){
			dto.exsistData();
		}
		return dto;
	}
	
	@Override
	public Optional<AttendanceItemDataGate> get(String path) {
		switch (path) {
		case DAILY_WORK_INFO_NAME:
			return Optional.ofNullable(this.workInfo);
		case DAILY_CALCULATION_ATTR_NAME:
			return Optional.ofNullable(this.calcAttr);
		case DAILY_AFFILIATION_INFO_NAME:
			return Optional.ofNullable(this.affiliationInfo);
		case DAILY_OUTING_TIME_NAME:
			return Optional.ofNullable(this.outingTime.orElse(null));
		case DAILY_BREAK_TIME_NAME:
			return Optional.ofNullable(this.breakTime.orElse(null));
		case DAILY_ATTENDANCE_TIME_NAME:
			return Optional.ofNullable(this.attendanceTime.orElse(null));
		case DAILY_ATTENDANCE_TIME_BY_WORK_NAME:
			return Optional.ofNullable(this.attendanceTimeByWork.orElse(null));
		case DAILY_ATTENDACE_LEAVE_NAME:
			return Optional.ofNullable(this.timeLeaving.orElse(null));
		case DAILY_SHORT_TIME_NAME:
			return Optional.ofNullable(this.shortWorkTime.orElse(null));
		case DAILY_SPECIFIC_DATE_ATTR_NAME:
			return Optional.ofNullable(this.specificDateAttr.orElse(null));
		case DAILY_ATTENDANCE_LEAVE_GATE_NAME:
			return Optional.ofNullable(this.attendanceLeavingGate.orElse(null));
		case DAILY_OPTIONAL_ITEM_NAME:
			return Optional.ofNullable(this.optionalItem.orElse(null));
		case DAILY_TEMPORARY_TIME_NAME:
			return Optional.ofNullable(this.temporaryTime.orElse(null));
		case DAILY_PC_LOG_INFO_NAME:
			return Optional.ofNullable(this.pcLogInfo.orElse(null));
		case DAILY_REMARKS_NAME:
			return Optional.ofNullable(this.remarks);
		case DAILY_SUPPORT_TIME_NAME:
			return Optional.ofNullable(this.ouenWorkTime.orElse(null));
		case DAILY_SNAPSHOT_NAME:
			return Optional.ofNullable(this.snapshot.orElse(null));
		case DAILY_SUPPORT_TIMESHEET_NAME:
			return Optional.ofNullable(this.ouenTimeSheet);
		default:
			return Optional.empty();
		}
	}
	
	@Override
	public void set(String path, AttendanceItemDataGate value) {
		switch (path) {
		case DAILY_WORK_INFO_NAME:
			this.workInfo = (WorkInformationOfDailyDto) value;
			break;
		case DAILY_CALCULATION_ATTR_NAME:
			this.calcAttr = (CalcAttrOfDailyPerformanceDto) value;
			break;
		case DAILY_AFFILIATION_INFO_NAME:
			this.affiliationInfo = (AffiliationInforOfDailyPerforDto) value;
			break;
		case DAILY_OUTING_TIME_NAME:
			this.outingTime = Optional.ofNullable((OutingTimeOfDailyPerformanceDto) value);
			break;
		case DAILY_BREAK_TIME_NAME:
			this.breakTime = Optional.ofNullable((BreakTimeDailyDto) value);
			break;
		case DAILY_ATTENDANCE_TIME_BY_WORK_NAME:/** まだ対応しない */
			this.attendanceTimeByWork = Optional.ofNullable((AttendanceTimeByWorkOfDailyDto) value);
			break;
		case DAILY_ATTENDACE_LEAVE_NAME:
			this.timeLeaving = Optional.ofNullable((TimeLeavingOfDailyPerformanceDto) value);
			break;
		case DAILY_SHORT_TIME_NAME:
			this.shortWorkTime = Optional.ofNullable((ShortTimeOfDailyDto) value);
			break;
		case DAILY_SPECIFIC_DATE_ATTR_NAME:
			this.specificDateAttr = Optional.ofNullable((SpecificDateAttrOfDailyPerforDto) value);
			break;
		case DAILY_ATTENDANCE_LEAVE_GATE_NAME:
			this.attendanceLeavingGate = Optional.ofNullable((AttendanceLeavingGateOfDailyDto) value);
			break;
		case DAILY_OPTIONAL_ITEM_NAME:
			this.optionalItem = Optional.ofNullable((OptionalItemOfDailyPerformDto) value);
			break;
		case DAILY_TEMPORARY_TIME_NAME:
			this.temporaryTime = Optional.ofNullable((TemporaryTimeOfDailyPerformanceDto) value);
			break;
		case DAILY_PC_LOG_INFO_NAME:
			this.pcLogInfo = Optional.ofNullable((PCLogOnInforOfDailyPerformDto) value);
			break;
		case DAILY_REMARKS_NAME:
			this.remarks = (RemarksOfDailyDto) value;
			break;
		case DAILY_ATTENDANCE_TIME_NAME:
			this.attendanceTime = Optional.ofNullable((AttendanceTimeDailyPerformDto) value);
			break;
		case DAILY_SUPPORT_TIME_NAME:
			this.ouenWorkTime = Optional.ofNullable((OuenWorkTimeOfDailyDto) value);
			break;
		case DAILY_SNAPSHOT_NAME:
			this.snapshot = Optional.ofNullable((SnapshotDto) value);
			break;
		case DAILY_SUPPORT_TIMESHEET_NAME:
			this.ouenTimeSheet = (OuenWorkTimeSheetOfDailyDto) value;
			break;
		default:
			break;
		}
	}

	@Override
	public AttendanceItemDataGate newInstanceOf(String path) {
		switch (path) {
		case DAILY_WORK_INFO_NAME:
			return new WorkInformationOfDailyDto();
		case DAILY_CALCULATION_ATTR_NAME:
			return new CalcAttrOfDailyPerformanceDto();
		case DAILY_AFFILIATION_INFO_NAME:
			return new AffiliationInforOfDailyPerforDto();
		case DAILY_OUTING_TIME_NAME:
			return new OutingTimeOfDailyPerformanceDto();
		case DAILY_BREAK_TIME_NAME:
			return new BreakTimeDailyDto();
		case DAILY_ATTENDANCE_TIME_NAME:
			return new AttendanceTimeDailyPerformDto();
		case DAILY_ATTENDANCE_TIME_BY_WORK_NAME:
			return new AttendanceTimeByWorkOfDailyDto();
		case DAILY_ATTENDACE_LEAVE_NAME:
			return new TimeLeavingOfDailyPerformanceDto();
		case DAILY_SHORT_TIME_NAME:
			return new ShortTimeOfDailyDto();
		case DAILY_SPECIFIC_DATE_ATTR_NAME:
			return new SpecificDateAttrOfDailyPerforDto();
		case DAILY_ATTENDANCE_LEAVE_GATE_NAME:
			return new AttendanceLeavingGateOfDailyDto();
		case DAILY_OPTIONAL_ITEM_NAME:
			return new OptionalItemOfDailyPerformDto();
		case DAILY_TEMPORARY_TIME_NAME:
			return new TemporaryTimeOfDailyPerformanceDto();
		case DAILY_PC_LOG_INFO_NAME:
			return new PCLogOnInforOfDailyPerformDto();
		case DAILY_REMARKS_NAME:
			return new RemarksOfDailyDto();
		case DAILY_SUPPORT_TIME_NAME:
			return new OuenWorkTimeOfDailyDto();
		case DAILY_SNAPSHOT_NAME:
			return new SnapshotDto();
		case DAILY_SUPPORT_TIMESHEET_NAME:
			return new OuenWorkTimeSheetOfDailyDto();
		default:
			return null;
		}
	}
	
	@Override
	public boolean isRoot() { return true; }
	
	@Override
	public boolean isContainer() { return true; }
	
//	@Override
//	@SuppressWarnings("unchecked")
//	public <T extends AttendanceItemDataGate> void set(String path, List<T> value) {
//		if (DAILY_SUPPORT_TIMESHEET_NAME.equals(path)) {
//			this.ouenTimeSheet = (OuenWorkTimeSheetOfDailyDto) value;
//		}
//	}	
	
//	@Override
//	@SuppressWarnings("unchecked")
//	public <T extends AttendanceItemDataGate> List<T> gets(String path) {
//		if (DAILY_SUPPORT_TIMESHEET_NAME.equals(path)) {
//			return (List<T>) this.ouenTimeSheet;
//		}
//		return new ArrayList<>();
//	}
}

