package nts.uk.ctx.at.shared.dom.scherec.statutory.worktime;

import java.util.List;
import java.util.Optional;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.algorithm.monthly.MonAndWeekStatutoryTime;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.algorithm.monthly.MonthlyFlexStatutoryLaborTime;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.algorithm.monthly.MonthlyStatutoryWorkingHours.RequireM1;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.algorithm.monthly.MonthlyStatutoryWorkingHours.RequireM4;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingSystem;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmploymentPeriodImported;

/**
 * 社員の法定労働時間を取得する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared(勤務予定、勤務実績).法定労働時間.法定労働時間（New）.社員の法定労働時間を取得する
 * @author lan_lt
 *
 */
public class GetLegalWorkTimeOfEmployeeService {

	/**
	 * 取得する
	 * @param require
	 * @param sid 社員ID
	 * @param period 期間
	 * @return
	 */
	public static Optional<LegalWorkTimeOfEmployee> get(Require require, String sid, DatePeriod period) {
		
		val employeementHists = require.getEmploymentHistories(sid, period);
		if(employeementHists.isEmpty())
			return Optional.empty();
		
		val employementCd = employeementHists.stream()
				.sorted((a, b) -> b.getDatePeriod().end().compareTo(a.getDatePeriod().end()))
				.findFirst().get().getEmploymentCd();					

		
		val wkCondItemOfEmp = require.getHistoryItemBySidAndBaseDate(sid, period.start());
		if(!wkCondItemOfEmp.isPresent()) 
			return Optional.empty();
		
		return getLegalWorkTimeOfEmployee(require, sid, period.start(), wkCondItemOfEmp.get().getLaborSystem(), employementCd);
	}
	
	/**
	 * 社員の法定労働時間を取得する
	 * @param require
	 * @param sid 社員ID
	 * @param baseDate 基準日
	 * @param workingSystem 労働制
	 * @param employmentCd 雇用コード
	 * @return
	 */
	private static Optional<LegalWorkTimeOfEmployee> getLegalWorkTimeOfEmployee(Require require, String sid
			, GeneralDate baseDate, WorkingSystem workingSystem, String employmentCd){
		val yearMonth = baseDate.yearMonth();
		
		 if(workingSystem == WorkingSystem.EXCLUDED_WORKING_CALCULATE)
			return Optional.empty();
		 
		 if(workingSystem == WorkingSystem.FLEX_TIME_WORK) {
			 return getLegalWorkTimeFlex(require, sid, baseDate, yearMonth, workingSystem, employmentCd);
		 }
		 
		 val monAndWeek = require.monAndWeekStatutoryTime(yearMonth, employmentCd, sid, baseDate, workingSystem);
		if(!monAndWeek.isPresent()) 
			return Optional.empty();
		
		return Optional.of(LegalWorkTimeOfEmployee.create(sid, monAndWeek.get().getWeeklyEstimateTime(),
				monAndWeek.get().getMonthlyEstimateTime()));
	}
	
	/**
	 * フレックスの法定時間を取得する
	 * @param require
	 * @param sid 社員ID
	 * @param baseDate 基準日
	 * @param yearMonth 年月度
	 * @param workingSystem 労働制
	 * @param employmentCd 雇用コード
	 * @return
	 */
	private static Optional<LegalWorkTimeOfEmployee> getLegalWorkTimeFlex(Require require, String sid, GeneralDate baseDate
			, YearMonth yearMonth , WorkingSystem workingSystem, String employmentCd){
//		val flexUse = require.getFlexStatutoryTime();
		
//		if(!flexUse.isPresent()) 
			return Optional.empty();
		
//		val flexMonAndWeek = require.flexMonAndWeekStatutoryTime(yearMonth, employmentCd, sid, baseDate);
//		val workTime = flexUse.get().getReference() == ReferencePredTimeOfFlex.FROM_RECORD?
//				flexMonAndWeek.getStatutorySetting(): flexMonAndWeek.getSpecifiedSetting();
//				
//		return Optional.of(LegalWorkTimeOfEmployee.createOnlyMonthTime(sid, workTime));
	}
	
	public static interface Require extends RequireM1, RequireM4{
		/**
		 * 社員を指定して年月日時点の履歴項目を取得する
		 * @param sid 社員ID
		 * @param baseDate 年月日
		 * @return
		 */
		Optional<WorkingConditionItem> getHistoryItemBySidAndBaseDate(String sid, GeneralDate baseDate);

		/**
		 * 雇用履歴を取得する( 社員ID, 期間 ): List<社員の雇用期間Imported>
		 * @param sid 社員ID
		 * @param datePeriod 期間
		 */
		List<EmploymentPeriodImported> getEmploymentHistories(String sid, DatePeriod datePeriod);
		
		/**
		 * フレックスの法定労働時間を取得する
		 * @param ym 年月度
		 * @param employmentCd 雇用コード
		 * @param employeeId 社員ID
		 * @param baseDate 基準日
		 * @return
		 */
		MonthlyFlexStatutoryLaborTime flexMonAndWeekStatutoryTime(YearMonth ym, String employmentCd, String employeeId, GeneralDate baseDate);
	
		/**
		 * 通常、変形の法定労働時間を取得する
		 * @param ym 年月度
		 * @param employmentCd 雇用コード
		 * @param employeeId 社員ID
		 * @param baseDate 基準日
		 * @param workingSystem 労働制
		 * @return
		 */
		Optional<MonAndWeekStatutoryTime> monAndWeekStatutoryTime(YearMonth ym, String employmentCd, String employeeId,  GeneralDate baseDate, WorkingSystem workingSystem);
	
		/**
		 * フレックス勤務所定労働時間取得を取得する
		 * @return
		 */
//		Optional<GetFlexPredWorkTime> getFlexStatutoryTime();
	}
}
