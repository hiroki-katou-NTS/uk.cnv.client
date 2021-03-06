package nts.uk.ctx.workflow.dom.adapter.bs;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.workflow.dom.adapter.bs.dto.ConcurrentEmployeeImport;
import nts.uk.ctx.workflow.dom.adapter.bs.dto.EmpInfoImport;
import nts.uk.ctx.workflow.dom.adapter.bs.dto.EmpInfoRQ18;
import nts.uk.ctx.workflow.dom.adapter.bs.dto.EmployeeImport;
import nts.uk.ctx.workflow.dom.adapter.bs.dto.PersonImport;
import nts.uk.ctx.workflow.dom.adapter.bs.dto.ResultRequest596Import;
import nts.uk.ctx.workflow.dom.adapter.bs.dto.StatusOfEmpImport;
import nts.uk.ctx.workflow.dom.adapter.bs.dto.StatusOfEmploymentImport;

public interface EmployeeAdapter {

	/**
	 * 「所属職場履歴」をすべて取得する
	 * get employee information by companyId, workplaceId and base date
	 * @param companyId　会社ID
	 * @param workplaceIds　職場IDリスト
	 * @param baseDate　基準日
	 * @return 社員情報
	 */
	List<EmployeeImport> findByWpkIds(String companyId, List<String> workplaceIds, GeneralDate baseDate);

	/**
	 * 「所属職場履歴」をすべて取得する
	 * get employee information by companyId, workplaceId and base date
	 * @param companyId　会社ID
	 * @param workplaceIds　職場IDリスト
	 * @param baseDate　基準日
	 * @return 社員情報
	 */
	List<EmployeeImport> findByWpkIdsWithParallel(String companyId, List<String> lstWkpDepId,
			GeneralDate baseDate, int sysAtr);
	
	/**
	 * ドメインモデル「職場別就業承認ルート」を取得する(lấy domain「職場別就業承認ルート」)
	 * 取得した所属職場ID＋その上位職場ID
	 * @param companyId
	 * @param employeeId
	 * @param date
	 * @return
	 */
	List<String> findWpkIdsBySid(String companyId, String employeeId, GeneralDate date);
	/**
	 * 基準日、会社IDに一致するすべての社員を取得する(在職中の社員）
	 * @param companyId
	 * @param baseDate
	 * @return
	 */
	List<EmployeeImport> getEmployeesAtWorkByBaseDate(String companyId , GeneralDate baseDate);
	
	/**
	 * Get employee Name
	 * @param sID
	 * @return
	 */
	String getEmployeeName(String sID);
	
	/**
	 * Gets the concurrent employee.
	 *
	 * @param companyId the company id
	 * @param jobId the job id
	 * @param baseDate the base date
	 * @return the concurrent employee
	 */
	// RequestList #77
	List<ConcurrentEmployeeImport> getConcurrentEmployee(String companyId, String jobId, GeneralDate baseDate);

	/**
	 * getEmployeeInformation
	 * @param sID
	 * @return
	 */
	PersonImport getEmployeeInformation(String sID);
	/**
	 * 指定社員が基準日に承認権限を持っているかチェックする 
	 * @param companyId
	 * @param employeeID
	 * @param date
	 * @return
	 */
	boolean canApprovalOnBaseDate(String companyId, String employeeID, GeneralDate date);
	
	boolean isEmployeeDelete(String sid);
	
	StatusOfEmploymentImport getStatusOfEmployment(String employeeID, GeneralDate referenceDate);
	//adapter RQ18
	Optional<EmpInfoRQ18> getEmpInfoByScd(String companyId, String employeeCode);
	
	public List<StatusOfEmpImport> getListAffComHistByListSidAndPeriod(List<String> sids, DatePeriod datePeriod);
	
	public List<ResultRequest596Import> getEmpDeletedLstBySids(List<String> sids);
	
	/**
	 * [No.571]職場の上位職場を基準職場を含めて取得する
	 *
	 * @param companyId
	 * @param baseDate
	 * @param workplaceId
	 * @return
	 */
	public List<String> getWorkplaceIdAndUpper(String companyId, GeneralDate baseDate, String workplaceId);
	
	/**
	 * [No.650]社員が所属している職場を取得する
	 * 社員と基準日から所属職場履歴項目を取得する
	 * @param employeeID
	 * @param date
	 * @return
	 */
	public Optional<String> getWkpBySidDate(String employeeID, GeneralDate date);
	
	/**
	 * 社員ID(List)から個人社員基本情報を取得
	 * @param lstSid
	 * @return
	 */
	// RequestList61
	List<EmpInfoImport> getEmpInfo(List<String> lstSid);
}
