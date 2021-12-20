package nts.uk.ctx.at.record.dom.monthly;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.at.record.dom.monthlyprocess.aggr.MonthlyAggregationEmployeeService;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionType;

/** 社員の月別実績を集計する */
public class MonthlyAggregateForEmployees {

	/** 集計する */
	public static List<AtomTask> aggregate(Require require, String cid, 
			List<String> sids, boolean canAggrWhenLock) {
		
		/** $実行ID = 新しいGUIDを作成する */
		String execId = IdentifierUtil.randomUniqueId();
		/** $cacheCarrier = new CacheCarrier() */
		CacheCarrier cacheCarrier = new CacheCarrier();
		/** $基準日 = システム日付	※全社員同じ基準日を使いたいので、ループの手前で一回だけ取得する */
		GeneralDate today = GeneralDate.today();
		
		/** $集計結果 = 社員ID一覧.map:	アルゴリズム.社員の月別実績を集計する*/
		return sids.stream().map(sid -> 
					MonthlyAggregationEmployeeService.aggregate(
							require, cacheCarrier, Optional.empty(), cid, sid, today, 
							execId, ExecutionType.NORMAL_EXECUTION, Optional.of(canAggrWhenLock))
						.getAtomTasks())
				.flatMap(List::stream).collect(Collectors.toList());
	}
	
	public static interface Require extends MonthlyAggregationEmployeeService.RequireM1 {}
}
