package nts.uk.ctx.at.record.dom.workrecord.operationsetting;

/**
 * 日別実績の機能制限
 */
public interface DayFuncControlRepository {
	
	public void add(DaiPerformanceFun daiPerformanceFun, IdentityProcess identityProcess, ApprovalProcess approvalProcess);
	
	public void update(DaiPerformanceFun daiPerformanceFun, IdentityProcess identityProcess, ApprovalProcess approvalProcess);
	
	void remove(String cid);
}
