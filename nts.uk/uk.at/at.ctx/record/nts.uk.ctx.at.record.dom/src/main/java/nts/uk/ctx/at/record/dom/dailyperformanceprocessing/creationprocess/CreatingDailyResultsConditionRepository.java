package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.creationprocess;

import java.util.Optional;

public interface CreatingDailyResultsConditionRepository {

	// [1] Insert(日別実績を作成する条件)
	void insert(CreatingDailyResultsCondition domain);

	// [2] Update(日別実績を作成する条件)
	void delete(CreatingDailyResultsCondition domain);

	// [3] find
	Optional<CreatingDailyResultsCondition> findByCid(String cid);
}
