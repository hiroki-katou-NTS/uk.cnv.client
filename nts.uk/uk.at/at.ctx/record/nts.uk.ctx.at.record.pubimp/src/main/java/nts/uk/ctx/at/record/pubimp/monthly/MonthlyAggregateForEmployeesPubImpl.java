package nts.uk.ctx.at.record.pubimp.monthly;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.task.tran.AtomTask;
import nts.uk.ctx.at.record.dom.monthly.MonthlyAggregateForEmployees;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.record.pub.monthly.MonthlyAggregateForEmployeesPub;

@Stateless
public class MonthlyAggregateForEmployeesPubImpl implements MonthlyAggregateForEmployeesPub {

	@Inject
	private RecordDomRequireService requireService;
	
	@Override
	public List<AtomTask> aggregate(String cid, List<String> sids, boolean canAggrWhenLock) {
		
		val require = requireService.createRequire();
		
		return MonthlyAggregateForEmployees.aggregate(require, cid, sids, canAggrWhenLock);
	}

}
