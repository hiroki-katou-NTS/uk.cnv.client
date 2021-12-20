package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.pastmonth;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.adapter.employment.BsEmploymentHistoryImport;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.algorithm.monthly.GetPeriodExcluseEntryRetireTime;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosurePeriod;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;

/** 月別実績過去月の集計期間を計算する */
public class AggregatePastMonthsPeriodService {

	/** 集計期間を判断する */
	public static List<ClosurePeriod> calcPeriod(RequireM3 require, CacheCarrier cacheCarrier, 
			String cid, String sid, GeneralDate aggrStartDate) {
		
		/** 過去月集計総集計期間を判断する */
		val aggrPeriod = getAggrPeriod(require, cacheCarrier, sid, aggrStartDate).orElse(null);
		if (aggrPeriod == null) {
			return new ArrayList<>();
		}
		
		/** 集計期間一覧を作る */
		List<ClosurePeriod> aggrPeriods = new ArrayList<>();
		
		/** 締め開始日を判断する */
		GeneralDate closureStartDate = aggrPeriod.start();
		
		while (closureStartDate.beforeOrEquals(aggrPeriod.end())) {
			
			/** 月集計期間を求める */
			val aggrMonthPeriod = getAggrMonthPeriod(require, cacheCarrier, cid, sid, 
					closureStartDate, aggrPeriod, aggrPeriods);
			
			if (!aggrMonthPeriod.isPresent() || aggrMonthPeriod.get().getPeriod().datesBetween().size() == 0) {
				
				return aggrPeriods;
			}
			
			/** 期間を集計期間一覧に入れる */
			aggrPeriods.add(aggrMonthPeriod.get());
			
			/** 締め開始日を判断する */
			closureStartDate = aggrMonthPeriod.get().getPeriod().end().addDays(1);
		}
		return aggrPeriods;
	}
	
	/** 月集計期間を求める*/
	private static Optional<ClosurePeriod> getAggrMonthPeriod(RequireM2 require, CacheCarrier cacheCarrier, 
			String cid, String sid, GeneralDate aggrStartDate, DatePeriod totalPeriod, List<ClosurePeriod> closurePeriods) {
		
		/** 指定した年月日時点の社員の締め期間を取得する */
		return getClosurePeriod(require, cacheCarrier, sid, aggrStartDate).flatMap(p -> {

			/** 総集計期間に含まれる期間を求める */
			val period = getElapsedIn(totalPeriod, p.getPeriod());
			
			/** [RQ31]社員所属雇用履歴を取得 */
			val employement = require.employmentHistory(cacheCarrier, cid, sid, aggrStartDate);
			
			/** 雇用履歴期間に含まれる期間を求める */
			Optional<DatePeriod> inEmploymentPeriod = !period.isPresent() ? Optional.empty() : 
					employement.flatMap(c -> getElapsedIn(c.getPeriod(), period.get()));
					
			/** 締め期間一覧にまだ含まれていない期間を求める */
			val aggrPeriod = inEmploymentPeriod.flatMap(c -> getNotInPeriod(closurePeriods, c));
			
			return aggrPeriod.map(c -> ClosurePeriod.of(p.getClosureId(), p.getClosureDate(), p.getYearMonth(), c));
		});
	}
	
	/** 締め期間一覧にまだ含まれていない期間を求める */
	private static Optional<DatePeriod> getNotInPeriod(List<ClosurePeriod> closurePeriods, DatePeriod period) {
		
		val containPeriod = closurePeriods.stream().filter(c -> c.getPeriod().contains(period)).findFirst();
		
		if (containPeriod.isPresent()) {
			
			if (period.endIsAfter(containPeriod.get().getPeriod())) {
				
				return Optional.of(new DatePeriod(containPeriod.get().getPeriod().end().addDays(1), period.end()));
			}
			
			return Optional.empty();
		}
		
		return Optional.of(period);
	}
	
	private static Optional<DatePeriod> getElapsedIn(DatePeriod total, DatePeriod target) {
		
		if (target.start().after(total.end()) || target.end().before(total.start()))
			return Optional.empty();
		
		val start = target.start().before(total.start()) ? total.start() : target.start();
		val end = target.end().after(total.end()) ? total.end() : target.end();
		
		return Optional.of(new DatePeriod(start, end));
	}

	/** 過去月集計総集計期間を判断する */
	private static Optional<DatePeriod> getAggrPeriod(RequireM1 require, CacheCarrier cacheCarrier, 
			String sid, GeneralDate aggrStartDate) {
		
		/** 指定した年月日時点の社員の締め期間を取得する */
		return getClosurePeriod(require, cacheCarrier, sid, aggrStartDate).flatMap(cp -> {
			/** 集計期間を判断する */
			DatePeriod period = cp.getPeriod();
			
			/** パラメータ。集計開始日とシステム年月日を比較する */
			if (aggrStartDate.before(GeneralDate.today())) {
				
				/** 指定した年月日時点の社員の締め期間を取得する */
				val todayClosurePeriod = getClosurePeriod(require, cacheCarrier, sid, GeneralDate.today());
				
				/** 集計期間を判断する */
				period = new DatePeriod(cp.getPeriod().start(), 
										todayClosurePeriod.map(c -> c.getPeriod().end())
														  .orElseGet(() -> cp.getPeriod().end()));
			}
			
			/** 入社前、退職後を期間から除く */
			return GetPeriodExcluseEntryRetireTime.getPeriodExcluseEntryRetireTime(require, cacheCarrier, sid, period);
		});
		
	}
	
	/** 指定した年月日時点の社員の締め期間を取得する */
	public static Optional<ClosurePeriod> getClosurePeriod(Require require, CacheCarrier cacheCarrier,
			String sid, GeneralDate aggrStartDate) {

		/** 社員に対応する処理締めを取得する */
		val closure = ClosureService.getClosureDataByEmployee(require, cacheCarrier, sid, aggrStartDate);
		if (closure == null) {
			return Optional.empty();
		}
		
		/** 指定した年月日時点の締め期間を取得する */
		return closure.getClosurePeriodByYmd(aggrStartDate);
	}
	
	public static interface RequireM3 extends RequireM2, RequireM1 {
		
	}
	
	public static interface RequireM2 extends Require {
		
		Optional<BsEmploymentHistoryImport> employmentHistory(CacheCarrier cacheCarrier, String companyId, 
				String employeeId, GeneralDate ymd);
	}
	
	public static interface RequireM1 extends Require, GetPeriodExcluseEntryRetireTime.Require {
		
	}
	
	public static interface Require extends ClosureService.RequireM3 {
		
	}
}
