package nts.uk.ctx.at.record.dom.monthlyclosureupdateprocess.remainnumberprocess;


import java.util.Optional;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.monthly.holidaymanagement.publicholiday.GetAggregationPeriodForPublicHoliday;
import nts.uk.ctx.at.record.dom.monthlyclosureupdateprocess.remainnumberprocess.annualleave.AnnualLeaveProcess;
import nts.uk.ctx.at.record.dom.monthlyclosureupdateprocess.remainnumberprocess.care.CareProcess;
import nts.uk.ctx.at.record.dom.monthlyclosureupdateprocess.remainnumberprocess.childcare.ChildCareProcess;
import nts.uk.ctx.at.record.dom.monthlyclosureupdateprocess.remainnumberprocess.compensatoryholiday.CompensatoryHolidayProcess;
import nts.uk.ctx.at.record.dom.monthlyclosureupdateprocess.remainnumberprocess.publicHoliday.PublicHolidayProcess;
import nts.uk.ctx.at.record.dom.monthlyclosureupdateprocess.remainnumberprocess.specialholiday.SpecialHolidayProcess;
import nts.uk.ctx.at.record.dom.monthlyclosureupdateprocess.remainnumberprocess.substitutionholiday.SubstitutionHolidayProcess;
import nts.uk.ctx.at.record.dom.monthlycommon.aggrperiod.AggrPeriodEachActualClosure;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.breakinfo.FixedRemainDataForMonthlyAgg;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

/**
 * 
 * @author HungTT - 月締め更新残数処理
 *
 */
public class MonthlyClosureRemainNumProcess {

	/**
	 * 残数処理
	 * @param period 実締め毎集計期間
	 * @param empId 社員ID
	 * @param attTimeMonthly 月別実績の勤怠時間
	 */
	public static AtomTask remainNumberProcess(RequireM1 require, CacheCarrier cacheCarrier,  String cid, 
			AggrPeriodEachActualClosure period, String empId,
			AttendanceTimeOfMonthly attTimeMonthly) {

		String companyId = AppContexts.user().companyId();
		
		/** アルゴリズム「月次処理用の暫定残数管理データを作成する」を実行する */
		val interimRemainMng = require.monthInterimRemainData(cacheCarrier, companyId, empId, period.getPeriod(),
												attTimeMonthly.getYearMonth(), period.getClosureId(), period.getClosureDate());
		
		Optional<DatePeriod> aggregationPeriodForPublicHoliday = 
				GetAggregationPeriodForPublicHoliday.getAggregationPeriodForPublicHoliday(require,
				cacheCarrier, companyId, empId,period.getYearMonth(), period.getPeriod());

		// Workを考慮した月次処理用の暫定残数管理データを作成する
		FixedRemainDataForMonthlyAgg interimDatasForPublicHoliday = new FixedRemainDataForMonthlyAgg();
		if (aggregationPeriodForPublicHoliday.isPresent())
			interimDatasForPublicHoliday = require.monthInterimRemainData(cacheCarrier, companyId, empId, 
					aggregationPeriodForPublicHoliday.get(),
					attTimeMonthly.getYearMonth(), period.getClosureId(), period.getClosureDate());

		
		/** 年休処理 */
		return AnnualLeaveProcess.annualHolidayProcess(require, cacheCarrier, cid, period, empId, interimRemainMng.getDaily())
				/** 振休処理 */
				.then(SubstitutionHolidayProcess.substitutionHolidayProcess(require, cacheCarrier, period, empId, interimRemainMng))
				/** 代休処理 */
				.then(CompensatoryHolidayProcess.compensatoryHolidayProcess(require, cacheCarrier, period, empId, interimRemainMng))
				/** 特別休暇処理 */
				.then(SpecialHolidayProcess.specialHolidayProcess(require, cacheCarrier, period, empId, interimRemainMng.getDaily()))
				/** 公休処理 */
				.then(PublicHolidayProcess.process(require, cacheCarrier, period, aggregationPeriodForPublicHoliday, empId, interimDatasForPublicHoliday.getDaily()))

				/** TODO: 60H超休処理 */
				
				/** 子の看護休暇処理 */
				.then(ChildCareProcess.process(require, cacheCarrier, period, empId, interimRemainMng.getDaily()))
				/** 介護休暇処理 */
				.then(CareProcess.process(require, cacheCarrier, period, empId, interimRemainMng.getDaily()));
	}
	
	public static interface RequireM1 extends AnnualLeaveProcess.Require, SubstitutionHolidayProcess.Require,
		CompensatoryHolidayProcess.Require, SpecialHolidayProcess.Require, PublicHolidayProcess.Require,
		ChildCareProcess.Require, CareProcess.Require{
		
		FixedRemainDataForMonthlyAgg monthInterimRemainData(CacheCarrier cacheCarrier, String cid, String sid,
				DatePeriod dateData, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate);
	}
}
