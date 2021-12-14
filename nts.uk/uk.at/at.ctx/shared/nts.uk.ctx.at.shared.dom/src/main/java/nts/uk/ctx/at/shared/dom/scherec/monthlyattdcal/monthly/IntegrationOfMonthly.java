package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.MonthlyDayoffRemainData;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeOfManagePeriod;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.affiliation.AffiliationInfoOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.anyitem.AnyItemOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.editstate.EditStateOfMonthlyPerformance;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.EmployeeMonthlyPerError;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.remarks.RemarksMonthlyRecord;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.absenceleave.AbsenceLeaveRemainData;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.AnnLeaRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.care.CareRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.childcare.ChildcareRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.publicholiday.PublicHolidayRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.reserveleave.RsvLeaRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.specialholiday.SpecialHolidayRemainData;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.weekly.AttendanceTimeOfWeekly;

/**
 * 月別実績(Work)
 * @author shuichi_ishida
 */
@Getter
@Setter
@AllArgsConstructor
public class IntegrationOfMonthly {

	/** 月別実績の勤怠時間 */
	private Optional<AttendanceTimeOfMonthly> attendanceTime;
	/** 月別実績の所属情報 */
	private Optional<AffiliationInfoOfMonthly> affiliationInfo;
	/** 月別実績の任意項目 */
	private List<AnyItemOfMonthly> anyItemList;
	/** 管理期間の36協定時間 */
	private Optional<AgreementTimeOfManagePeriod> agreementTime;
	/** 年休月別残数データ */
	private Optional<AnnLeaRemNumEachMonth> annualLeaveRemain;
	/** 積立年休月別残数データ */
	private Optional<RsvLeaRemNumEachMonth> reserveLeaveRemain;
	/** 振休月別残数データ */
	private Optional<AbsenceLeaveRemainData> absenceLeaveRemain;
	/** 代休月別残数データ*/
	private Optional<MonthlyDayoffRemainData> monthlyDayoffRemain;
	/** 特別休暇月別残数データ */
	private List<SpecialHolidayRemainData> specialLeaveRemain;
	/** 週別実績の勤怠時間 */
	private List<AttendanceTimeOfWeekly> attendanceTimeOfWeek;
	/** 社員の月別実績エラー一覧 */
	private List<EmployeeMonthlyPerError> employeeMonthlyPerError;
	/** 月別実績の備考 */
	private List<RemarksMonthlyRecord> remarks;
	/** 介護休暇月別残数データ */
	private Optional<CareRemNumEachMonth> care;
	/** 子の看護休暇月別残数データ*/
	private Optional<ChildcareRemNumEachMonth> childCare;
	/** 公休月別残数データ */
	private Optional<PublicHolidayRemNumEachMonth> publicHolidayLeaveRemain;
	/** 月別実績の編集状態 */
	private List<EditStateOfMonthlyPerformance> editState;
	
	public IntegrationOfMonthly(){
		this.attendanceTime = Optional.empty();
		this.affiliationInfo = Optional.empty();
		this.anyItemList = new ArrayList<>();
		this.agreementTime = Optional.empty();
		this.annualLeaveRemain = Optional.empty();
		this.reserveLeaveRemain = Optional.empty();
		this.absenceLeaveRemain = Optional.empty();
		this.monthlyDayoffRemain = Optional.empty();
		this.specialLeaveRemain = new ArrayList<>();
		this.attendanceTimeOfWeek = new ArrayList<>();
		this.employeeMonthlyPerError = new ArrayList<>();
		this.remarks = new ArrayList<>();
		this.care = Optional.empty();
		this.childCare = Optional.empty();
		this.publicHolidayLeaveRemain = Optional.empty();
		this.editState = new ArrayList<>();
	}
	
	/**
	 * コンストラクタ
	 * @param attendanceTime 月別実績の勤怠時間
	 * @param affiliationInfo 月別実績の所属情報
	 * @param anyItemList 月別実績の任意項目
	 * @param agreementTimeList 管理期間の36協定時間
	 * @param annualLeaveRemain 年休月別残数データ
	 * @param reserveLeaveRemain 積立年休月別残数データ
	 * @param absenceLeaveRemain 振休月別残数データ
	 * @param monthlyDayoffRemain 代休月別残数データ
	 * @param specialLeaveRemainList 特別休暇月別残数データ
	 * @param remarks 月別実績の備考
	 * @param care 介護休暇月別残数データ
	 * @param childCare 子の看護月別残数データ
	 */
	public IntegrationOfMonthly(
			Optional<AttendanceTimeOfMonthly> attendanceTime,
			Optional<AffiliationInfoOfMonthly> affiliationInfo,
			List<AnyItemOfMonthly> anyItemList,
			Optional<AgreementTimeOfManagePeriod> agreementTimeList,
			Optional<AnnLeaRemNumEachMonth> annualLeaveRemain,
			Optional<RsvLeaRemNumEachMonth> reserveLeaveRemain,
			Optional<AbsenceLeaveRemainData> absenceLeaveRemain,
			Optional<MonthlyDayoffRemainData> monthlyDayoffRemain,
			List<SpecialHolidayRemainData> specialLeaveRemainList,
			List<RemarksMonthlyRecord> remarks,
			Optional<CareRemNumEachMonth> care,
			Optional<ChildcareRemNumEachMonth> childCare,
			Optional<PublicHolidayRemNumEachMonth> publicHoliday){
	
		this.attendanceTime = attendanceTime;
		this.affiliationInfo = affiliationInfo;
		this.anyItemList = anyItemList;
		this.agreementTime = agreementTimeList;
		this.annualLeaveRemain = annualLeaveRemain;
		this.reserveLeaveRemain = reserveLeaveRemain;
		this.absenceLeaveRemain = absenceLeaveRemain;
		this.monthlyDayoffRemain = monthlyDayoffRemain;
		this.specialLeaveRemain = specialLeaveRemainList;
		this.remarks = remarks;
		this.care = care;
		this.childCare = childCare;
		this.publicHolidayLeaveRemain = publicHoliday;
		this.editState = new ArrayList<>();
	}
}
