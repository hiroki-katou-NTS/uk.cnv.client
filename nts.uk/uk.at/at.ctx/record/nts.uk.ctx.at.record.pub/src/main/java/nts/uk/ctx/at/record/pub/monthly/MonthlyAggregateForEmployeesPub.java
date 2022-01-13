package nts.uk.ctx.at.record.pub.monthly;

import java.util.List;

import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.task.tran.AtomTask;

/** 社員の月別実績を集計する */
public interface MonthlyAggregateForEmployeesPub {

	/** 集計する */
	public List<AtomTask> aggregate(CacheCarrier cache, String cid, List<String> sids, boolean canAggrWhenLock);
}
