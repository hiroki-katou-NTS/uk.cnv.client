/**
 * 
 */
package nts.uk.ctx.hr.notice.dom.report.registration.person;

import java.util.List;

/**
 * @author laitv
 *
 */
public interface ApprovalPersonReportRepository {

	List<ApprovalPersonReport> getListDomainByReportId(String cid, String reprtId);
	
	List<ApprovalPersonReport> getListDomainByReportId(int reprtId);
	
	List<ApprovalPersonReport> getListDomainByReportIdAndSid(int reprtId, String approverId);
	
	boolean checkExit(int reportID, int phaseNum, int aprNum, String cid);
	
	void add(ApprovalPersonReport domain);
	
	void addAll(List<ApprovalPersonReport> domains);
	
	void updateAll(List<ApprovalPersonReport> domains);
	
	void update(ApprovalPersonReport domain);
	
	void delete(int reportID, int phaseNum, int aprNum, String cid);
	
	void deleteByReportId(int reportID, String cid);
}
