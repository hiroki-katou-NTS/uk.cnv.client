package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.MonthlyCalculation;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.MonthlyCalculation.AgreementTimeResult;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

/** 36協定時間の集計 */
public class AgreementTimeAggregateService {

	/** 36協定時間の集計 */
	public static List<AgreementTimeResult> aggregate(Require require, CacheCarrier cacheCarrier,
			String cid, String sid, YearMonth ym, ClosureId closureId, ClosureDate closureDate,
			DatePeriod period, MonAggrCompanySettings companySets, MonAggrEmployeeSettings employeeSets,
			MonthlyCalculatingDailys monthlyCalcDailys, MonthlyOldDatas monthlyOldDatas,
			Optional<MonthlyCalculation> basicCalced) {
		
		List<AgreementTimeResult> result = new ArrayList<>();
		
		// 36協定時間の集計
		MonthlyCalculation monthlyCalculationForAgreement = new MonthlyCalculation();

		val agreementTime = monthlyCalculationForAgreement.aggregateAgreementTime(require, cacheCarrier,
				cid, sid, ym, closureId, closureDate, period, Optional.empty(), Optional.empty(), 
				Optional.empty(), companySets, employeeSets, monthlyCalcDailys, monthlyOldDatas, basicCalced);

		result.add(agreementTime);

		// 36協定運用設定を取得
		companySets.getAgreementOperationSet().ifPresent(aos -> {
			
			if (!closureDate.equals(aos.getClosureDate()) && // 締めが異なる かつ
					period.start().after(employeeSets.getEmployee().getEntryDate())) { // 開始日が入社日より後の時

				// 集計期間を一ヶ月手前にずらす
				YearMonth prevYM = ym.addMonths(-1);
				GeneralDate prevEnd = period.start().addDays(-1);
				GeneralDate prevStart = prevEnd.addMonths(-1).addDays(1);
				DatePeriod prevPeriod = new DatePeriod(prevStart, prevEnd);

				// 36協定時間の集計
				MonthlyOldDatas prevOldDatas = MonthlyOldDatas.loadData(require, sid, prevYM, closureId, closureDate,
						Optional.empty());
				MonthlyCalculation prevCalculationForAgreement = new MonthlyCalculation();

				val prevAgreTime = prevCalculationForAgreement.aggregateAgreementTime(require,
						cacheCarrier, cid, sid, prevYM, closureId, closureDate,
						prevPeriod, Optional.empty(), Optional.empty(), Optional.empty(), companySets,
						employeeSets, monthlyCalcDailys, prevOldDatas, Optional.empty());

				result.add(prevAgreTime);
			}
		});
		
		return result;
	}
	
	public static interface Require extends MonthlyCalculation.RequireM2, MonthlyOldDatas.RequireM1 {
		
	}
}
