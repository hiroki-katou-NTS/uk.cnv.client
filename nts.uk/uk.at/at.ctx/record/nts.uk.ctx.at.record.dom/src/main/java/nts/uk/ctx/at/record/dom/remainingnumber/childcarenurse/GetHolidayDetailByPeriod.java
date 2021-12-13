package nts.uk.ctx.at.record.dom.remainingnumber.childcarenurse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.GetInterimRemainData;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.DailyInterimRemainMngData;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.ReferenceAtr;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.AggregateMonthlyRecordService.RequireM1;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.GetClosureStartForEmployee;

/**
 * 
 * DS : 期間内の休暇使用明細を取得する
 * http://192.168.50.14:81/domain/?type=dom&dom=%E6%9C%9F%E9%96%93%E5%86%85%E3%81%AE%E4%BC%91%E6%9A%87%E4%BD%BF%E7%94%A8%E6%98%8E%E7%B4%B0%E3%82%92%E5%8F%96%E5%BE%97%E3%81%99%E3%82%8B
 * 
 * @author tutk
 *
 */
public class GetHolidayDetailByPeriod {

	/**
	 * 
	 * @param require
	 * @param companyId    会社ID
	 * @param employeeId   社員ID
	 * @param period       期間
	 * @param referenceAtr 参照先区分
	 */
	public static List<DailyInterimRemainMngData> getHolidayDetailByPeriod(Require require, String companyId,
			String employeeId, DatePeriod period, ReferenceAtr referenceAtr) {
		if (referenceAtr == ReferenceAtr.RECORD) {
			return getByRecord(require, companyId, employeeId, period);
		}
		return getByAppAndSche(require, companyId, employeeId, period);

	}

	/**
	 * [prv-1] 実績のみを参照して取得
	 */
	private static List<DailyInterimRemainMngData> getByRecord(Require require, String companyId, String employeeId,
			DatePeriod period) {
		CacheCarrier cacheCarrier = new CacheCarrier();
		List<DailyInterimRemainMngData> data = require.mapInterimRemainData(require, cacheCarrier, companyId,
				employeeId, period);
		return data;
	}

	/**
	 * [prv-2] 予定・申請も参照して取得
	 */
	private static List<DailyInterimRemainMngData> getByAppAndSche(Require require, String companyId, String employeeId,
			DatePeriod period) {
		List<DailyInterimRemainMngData> dataResult = new ArrayList<>();
		dataResult.addAll(obtainedPastPeriod(require, companyId, employeeId, period));
		dataResult.addAll(obtainedPeriodAfterStartDateClosure(require, companyId, employeeId, period));
		return dataResult;
	}

	/**
	 * [prv-3] 過去の期間から取得
	 */
	private static List<DailyInterimRemainMngData> obtainedPastPeriod(Require require, String companyId,
			String employeeId, DatePeriod period) {
		CacheCarrier cacheCarrier = new CacheCarrier();
		Optional<GeneralDate> ymd = require.algorithm(require, cacheCarrier, employeeId);
		if (!ymd.isPresent()) {
			return new ArrayList<>();
		}
		DatePeriod periodNew = new DatePeriod(period.start(), ymd.get().addDays(-1));
		List<DailyInterimRemainMngData> data = require.mapInterimRemainData(require, cacheCarrier, companyId,
				employeeId, periodNew);
		return data;

	}

	/**
	 * [prv-4] 締め開始日以降の期間から取得
	 */
	private static List<DailyInterimRemainMngData> obtainedPeriodAfterStartDateClosure(Require require,
			String companyId, String employeeId, DatePeriod period) {
		CacheCarrier cacheCarrier = new CacheCarrier();
		Optional<GeneralDate> ymd = require.algorithm(require, cacheCarrier, employeeId);
		if (!ymd.isPresent()) {
			return new ArrayList<>();
		}

		DatePeriod periodNew = new DatePeriod(ymd.get(), period.end());
		List<DailyInterimRemainMngData> data = GetInterimRemainData.getInterimRemainData(require, employeeId, periodNew);
		
		return data;

	}

	public static interface Require extends RequireM1,GetClosureStartForEmployee.RequireM1,GetInterimRemainData.Require {
		/**
		 * [R-1]暫定残数管理データを作成する AggregateMonthlyRecordService
		 */
		List<DailyInterimRemainMngData> mapInterimRemainData(RequireM1 require, CacheCarrier cacheCarrier, String cid,
				String sid, DatePeriod datePeriod);

		/**
		 * [R-2]締め開始日を取得する GetClosureStartForEmployee
		 * 
		 * @return
		 */
		Optional<GeneralDate> algorithm(GetClosureStartForEmployee.RequireM1 require, CacheCarrier cacheCarrier, String employeeId);
	}
}
