package nts.uk.ctx.at.shared.dom.scherec.statutory.worktime;

import java.util.List;
import java.util.Optional;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.algorithm.monthly.MonAndWeekStatutoryTime;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.algorithm.monthly.MonthlyFlexStatutoryLaborTime;
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

		
		val wkCondItemOfEmp = require.getHistoryItemBySidAndBaseDate(sid, period.end());
		if(!wkCondItemOfEmp.isPresent()) 
			return Optional.empty();
		
		return getLegalWorkTimeOfEmployee(require, sid, period.end(), wkCondItemOfEmp.get().getLaborSystem(), employementCd);
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
			 val flexMonAndWeek = require.flexMonAndWeekStatutoryTime(yearMonth, employmentCd, sid, baseDate);
			 return Optional.of(LegalWorkTimeOfEmployee.createOnlyMonthTime(sid, flexMonAndWeek.getStatutorySetting()));
		 }
		 
		 val monAndWeek = require.monAndWeekStatutoryTime(yearMonth, employmentCd, sid, baseDate, workingSystem);
		if(!monAndWeek.isPresent()) 
			return Optional.empty();
		
		return Optional.of(LegalWorkTimeOfEmployee.create(sid, monAndWeek.get().getWeeklyEstimateTime(),
				monAndWeek.get().getMonthlyEstimateTime()));
	}
	
	public static interface Require{
		/**
		 * 社員を指定して年月日時点の履歴項目を取得する
		 * @param sid 社員ID
		 * @param baseDate 年月日
		 * @return
		 */
		Optional<WorkingConditionItem> getHistoryItemBySidAndBaseDate(String sid, GeneralDate baseDate);

		/**
		 * 雇用履歴を取得する( 社員ID, 期間 )
		 * @param sid 社員ID
		 * @param datePeriod 期間
		 * @return
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

	}
}