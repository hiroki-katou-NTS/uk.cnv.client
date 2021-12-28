package nts.uk.ctx.at.record.dom.daily;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.actualworkinghours.AttendanceTimeOfDailyPerformance;
import nts.uk.ctx.at.record.dom.actualworkinghours.daily.workrecord.AttendanceTimeByWorkOfDaily;
import nts.uk.ctx.at.record.dom.affiliationinformation.AffiliationInforOfDailyPerfor;
import nts.uk.ctx.at.record.dom.breakorgoout.BreakTimeOfDailyPerformance;
import nts.uk.ctx.at.record.dom.breakorgoout.OutingTimeOfDailyPerformance;
import nts.uk.ctx.at.record.dom.calculationattribute.CalAttrOfDailyPerformance;
import nts.uk.ctx.at.record.dom.daily.attendanceleavinggate.AttendanceLeavingGateOfDaily;
import nts.uk.ctx.at.record.dom.daily.attendanceleavinggate.PCLogOnInfoOfDaily;
import nts.uk.ctx.at.record.dom.daily.optionalitemtime.AnyItemValueOfDaily;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeOfDaily;
import nts.uk.ctx.at.record.dom.daily.remarks.RemarksOfDailyPerform;
import nts.uk.ctx.at.record.dom.editstate.EditStateOfDailyPerformance;
import nts.uk.ctx.at.record.dom.raisesalarytime.SpecificDateAttrOfDailyPerfor;
import nts.uk.ctx.at.record.dom.shorttimework.ShortTimeOfDailyPerformance;
import nts.uk.ctx.at.record.dom.workinformation.WorkInfoOfDailyPerformance;
import nts.uk.ctx.at.record.dom.worktime.TemporaryTimeOfDailyPerformance;
import nts.uk.ctx.at.record.dom.worktime.TimeLeavingOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.EmployeeDailyPerError;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.snapshot.SnapShot;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;

public interface DailyRecordAdUpService {
	// ドメインモデル「日別実績の勤務情報」を更新する
	public void adUpWorkInfo(WorkInfoOfDailyPerformance workInfo);

	// ドメインモデル「日別実績の所属情報」を更新する
	public void adUpAffilicationInfo(AffiliationInforOfDailyPerfor affiliationInfor);

	// ドメインモデル「日別実績の計算区分」を更新する
	public void adUpCalAttr(CalAttrOfDailyPerformance calAttr);

	// ドメインモデル「日別実績の出退勤」を更新する
	public void adUpTimeLeaving(Optional<TimeLeavingOfDailyPerformance> attendanceLeave);

	// ドメインモデル「日別実績の休憩時間帯」を更新する
	public void adUpBreakTime(BreakTimeOfDailyPerformance breakTime);

	// ドメインモデル「日別実績の外出時間帯」を更新する
	public void adUpOutTime(Optional<OutingTimeOfDailyPerformance> outingTime);

	// ドメインモデル「日別実績の短時間勤務時間帯」を更新する
	public void adUpShortTime(Optional<ShortTimeOfDailyPerformance> shortTime);

	// ドメインモデル「日別実績の臨時出退勤」を更新する
	public void adUpTemporaryTime(Optional<TemporaryTimeOfDailyPerformance> tempTime);

	// ドメインモデル「日別実績の入退門」を更新する
	public void adUpAttendanceLeavingGate(Optional<AttendanceLeavingGateOfDaily> attendanceLeavingGate);

	// ドメインモデル「日別実績の勤怠時間」を更新する
	public void adUpAttendanceTime(Optional<AttendanceTimeOfDailyPerformance> attendanceTimeOfDailyPerformance);

	// ドメインモデル「日別実績の特定日区分」を更新する
	public void adUpSpecificDate(Optional<SpecificDateAttrOfDailyPerfor> specDateAttr);

	// ドメインモデル「日別実績の編集状態」を更新する
	public void adUpEditState(List<EditStateOfDailyPerformance> editState);

	public void clearExcludeEditState(List<EditStateOfDailyPerformance> editState);

	// ドメインモデル「日別実績の任意項目」を更新する
	public void adUpAnyItem(Optional<AnyItemValueOfDaily> anyItemValue);

	//// ドメインモデル「日別実績の作業別勤怠時間」を更新する
	public void adUpAttendanceTimeByWork(Optional<AttendanceTimeByWorkOfDaily> attendancetimeByWork);

	// ドメインモデル「日別実績のPCログオン情報」を更新する
	public void adUpPCLogOn(Optional<PCLogOnInfoOfDaily> pcLogOnInfo);

	// ドメインモデル「日別実績の備考」を更新する
	public void adUpRemark(List<RemarksOfDailyPerform> remarks);
	
	// ドメインモデル「日別実績の応援作業別勤怠時間」を更新する
	public void adUpOuenWorkTime(Optional<OuenWorkTimeOfDaily> ouen);

	//ドメインモデル「スナップショット」を更新する
	public void adUpSnapshot(String sid, GeneralDate ymd, SnapShot snapshot);
	
	// ドメインモデル「日別勤怠の応援作業時間帯」を更新する
	public void adUpSupportTime(String sid, GeneralDate ymd, List<OuenWorkTimeSheetOfDailyAttendance> ouenTimeSheet);
	
	/**
	 * ドメインモデル「社員の日別実績エラー一覧」を更新する
	 * 
	 * @param errors         domain error
	 * @param lstPairRemove  List<Pair<employeeId, date>>
	 * @param hasRemoveError has remove error
	 */
	public default void adUpEmpError(List<EmployeeDailyPerError> errors, List<Pair<String, GeneralDate>> lstPairRemove) {
		adUpEmpError(errors, lstPairRemove, true);
	}
	public void adUpEmpError(List<EmployeeDailyPerError> errors, List<Pair<String, GeneralDate>> lstPairRemove,
			boolean hasRemoveError);

	public List<IntegrationOfDaily> adTimeAndAnyItemAdUp(List<IntegrationOfDaily> dailys);

	/**
	 * エラーで本人確認と上司承認を解除する
	 * @param domainDaily
	 * @param iPUSOpt
	 * @param approvalSet
	 */
	public void removeConfirmApproval(List<IntegrationOfDaily> domainDaily);

	public default void addAllDomain(IntegrationOfDaily domain) {

		// ドメインモデル「日別実績の勤務情報」を更新する
		adUpWorkInfo(
				new WorkInfoOfDailyPerformance(domain.getEmployeeId(), domain.getYmd(), domain.getWorkInformation()));
		// ドメインモデル「日別実績の所属情報」を更新する
		adUpAffilicationInfo(new AffiliationInforOfDailyPerfor(domain.getEmployeeId(), domain.getYmd(),
				domain.getAffiliationInfor()));

		// ドメインモデル「日別実績の計算区分」を更新する
		adUpCalAttr(new CalAttrOfDailyPerformance(domain.getEmployeeId(), domain.getYmd(), domain.getCalAttr()));

		// ドメインモデル「日別実績の出退勤」を更新する
		domain.getAttendanceLeave().ifPresent(x -> adUpTimeLeaving(
				Optional.of(new TimeLeavingOfDailyPerformance(domain.getEmployeeId(), domain.getYmd(), x))));

		// ドメインモデル「日別実績の休憩時間帯」を更新する
		adUpBreakTime(new BreakTimeOfDailyPerformance(domain.getEmployeeId(), domain.getYmd(), domain.getBreakTime()));

		// ドメインモデル「日別実績の外出時間帯」を更新する
		adUpOutTime(domain.getOutingTime()
				.map(x -> new OutingTimeOfDailyPerformance(domain.getEmployeeId(), domain.getYmd(), x)));

		// ドメインモデル「日別実績の短時間勤務時間帯」を更新する
		domain.getShortTime().ifPresent(x -> adUpShortTime(
				Optional.of(new ShortTimeOfDailyPerformance(domain.getEmployeeId(), domain.getYmd(), x))));

		// ドメインモデル「日別実績の臨時出退勤」を更新する
		domain.getTempTime().ifPresent(x -> adUpTemporaryTime(
				Optional.of(new TemporaryTimeOfDailyPerformance(domain.getEmployeeId(), domain.getYmd(), x))));

		// ドメインモデル「日別実績の入退門」を更新する
		domain.getAttendanceLeavingGate().ifPresent(x -> adUpAttendanceLeavingGate(
				Optional.of(new AttendanceLeavingGateOfDaily(domain.getEmployeeId(), domain.getYmd(), x))));

		// ドメインモデル「日別実績の特定日区分」を更新する
		domain.getSpecDateAttr().ifPresent(x -> adUpSpecificDate(
				Optional.of(new SpecificDateAttrOfDailyPerfor(domain.getEmployeeId(), domain.getYmd(), x))));

// ※　後のadTimeAndAnyItemAdUpの中で更新するようになったため不要(2020.12.18 shuichi_ishita)
//		// ドメインモデル「日別実績の編集状態」を更新する
//		adUpEditState(domain.getEditState().stream()
//				.map(x -> new EditStateOfDailyPerformance(domain.getEmployeeId(), domain.getYmd(), x))
//				.collect(Collectors.toList()));

		// ドメインモデル「日別実績のPCログオン情報」を更新する
		domain.getPcLogOnInfo().ifPresent(
				x -> adUpPCLogOn(Optional.of(new PCLogOnInfoOfDaily(domain.getEmployeeId(), domain.getYmd(), x))));

		// ドメインモデル「日別実績の備考」を更新する
		adUpRemark(domain.getRemarks().stream()
				.map(x -> new RemarksOfDailyPerform(domain.getEmployeeId(), domain.getYmd(), x))
				.collect(Collectors.toList()));
		// ドメインモデル「日別勤怠の応援作業時間帯」を更新する
		//adUpSupportTime(domain.getEmployeeId(), domain.getYmd(), domain.getOuenTimeSheet());

		// ドメインモデル「日別実績の応援作業別勤怠時間」を更新する
		adUpOuenWorkTime(Optional.of(OuenWorkTimeOfDaily.create(domain.getEmployeeId(), domain.getYmd(), domain.getOuenTime())));

		adUpEmpError(domain.getEmployeeError(),  Arrays.asList(Pair.of(domain.getEmployeeId(), domain.getYmd())));

		adTimeAndAnyItemAdUp(Arrays.asList(domain));
	}
}
