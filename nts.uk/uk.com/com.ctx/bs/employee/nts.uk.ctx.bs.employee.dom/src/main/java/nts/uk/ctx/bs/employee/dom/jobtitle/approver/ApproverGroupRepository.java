package nts.uk.ctx.bs.employee.dom.jobtitle.approver;

import java.util.List;

public interface ApproverGroupRepository {
	
	public List<ApproverGroup> findAll(String companyID);
	
	public void insert(ApproverGroup approverGroup);
	
	public void update(ApproverGroup approverGroup);
	
	public void delete(ApproverGroup approverGroup);
	
	public List<ApproverGInfo> getAll(String companyID);
	
	public List<ApproverGInfo> findByCd(String companyID, List<String> jobGCd);
	
}
