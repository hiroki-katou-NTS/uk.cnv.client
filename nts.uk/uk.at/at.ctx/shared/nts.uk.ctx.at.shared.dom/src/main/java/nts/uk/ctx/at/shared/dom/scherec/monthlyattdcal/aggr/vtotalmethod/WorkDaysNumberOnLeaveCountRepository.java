package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.vtotalmethod;

public interface WorkDaysNumberOnLeaveCountRepository {

	// [1] Insert(会社ID, 出勤日数としてカウントする休暇の種類)																							
	void insert(String cid, int leaveType);
	
	// [2] Delete(会社ID, 出勤日数としてカウントする休暇の種類)																							
	void delete(String cid, int leaveType);
	
	// [3] 取得する																							
	WorkDaysNumberOnLeaveCount findByCid(String cid);
}
