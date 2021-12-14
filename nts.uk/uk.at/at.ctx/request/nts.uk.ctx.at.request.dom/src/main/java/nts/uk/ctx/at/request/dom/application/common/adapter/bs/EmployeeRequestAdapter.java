package nts.uk.ctx.at.request.dom.application.common.adapter.bs;

import java.util.List;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.AffWorkplaceImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.ConcurrentEmployeeRequest;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.EmployeeEmailImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.PersonEmpBasicInfoImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.PesionInforImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.SEmpHistImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.SWkpHistImport;

public interface EmployeeRequestAdapter {
	
	/**
	 * 所属職場を含む上位職場を取得
	 *
	 * @param companyId the company id
	 * @param sid the employee id
	 * @param date the date
	 * @return the list
	 */
	// RequestList #65
	List<String> findWpkIdsBySid(String companyId, String sid, GeneralDate date);

	/**
	 * Get employee Name
	 * @param sID
	 * @return
	 */
	String getEmployeeName(String sID);
	
	
	PesionInforImport getEmployeeInfor(String sID);
	
	
	String empEmail(String sID);
	
	List<ConcurrentEmployeeRequest> getConcurrentEmployee(String companyId, String jobId, GeneralDate baseDate);
	
	SEmpHistImport getEmpHist(String companyId, String employeeId,
			GeneralDate baseDate);
	
	//社員の雇用履歴を全て取得する
	List<SEmpHistImport> getEmpHist(String companyId, String employeeId);
	
	SWkpHistImport getSWkpHistByEmployeeID(String employeeId, GeneralDate baseDate);
	
	/**
	 * 承認状況社員メールアドレス取得
	 * @param sIds 社員ID
	 * @return 取得社員ID＜社員ID、社員名、メールアドレス＞
	 */
	List<EmployeeEmailImport> getApprovalStatusEmpMailAddr(List<String> sIds);
	
	/**
	 * RequestList #120
	 * @param workplaceId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<AffWorkplaceImport> getListSIdByWkpIdAndPeriod(String workplaceId, GeneralDate startDate, GeneralDate endDate);
	
	List<PersonEmpBasicInfoImport> getPerEmpBasicInfo(String companyId, List<String> employeeIds);

	/**
	 * [No.571]職場の上位職場を基準職場を含めて取得する
	 *
	 * @param companyId
	 * @param baseDate
	 * @param workplaceId
	 * @return
	 */
	public List<String> getWorkplaceIdAndUpper(String companyId, GeneralDate baseDate, String workplaceId);
	
	public String getAffWkpHistItemByEmpDate(String employeeID, GeneralDate date);
}
