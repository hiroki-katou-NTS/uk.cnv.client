package nts.uk.ctx.at.shared.dom.worktime.filtercriteria;

import java.util.List;

public interface WorkHoursFilterConditionRepository {

	// [1]  insert(就業時間帯の絞り込み条件)																									
	void insert(WorkHoursFilterCondition domain);
	
	// [2]  update(就業時間帯の絞り込み条件)																									
	void update(WorkHoursFilterCondition domain);
	
	// [3]  delete(就業時間帯の絞り込み条件)																									
	void delete(WorkHoursFilterCondition domain);
	
	// [4]取得する																									
	List<WorkHoursFilterCondition> findByCid(String cid);
}
