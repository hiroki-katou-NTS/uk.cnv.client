package nts.uk.file.at.app.export.attendanceitemprepare;

import java.util.List;
import java.util.Optional;

import nts.uk.ctx.at.record.dom.confirmemployment.RestrictConfirmEmployment;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.ApprovalProcess;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.DaiPerformanceFun;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.FormatPerformance;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.IdentityProcess;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.MonPerformanceFun;
/**
 * 
 * @author Hoidd 2
 *
 */
public interface OperationExcelRepo {
	/**
	 * 
	 * @param companyId
	 * @return
	 */
	Optional<DaiPerformanceFun> getDaiPerformanceFunById(String companyId);
	/**
	 * 
	 * @param companyId
	 * @return
	 */
	Optional<MonPerformanceFun> getMonPerformanceFunById(String companyId);
	/**
	 * 
	 * @param companyId
	 * @return
	 */
	Optional<FormatPerformance> getFormatPerformanceById(String companyId);
	/**
	 * 
	 * @param companyId
	 * @return
	 */
	Optional<IdentityProcess> getIdentityProcessById(String companyId);
	/**
	 * 
	 * @param companyId
	 * @return
	 */
	Optional<ApprovalProcess> getApprovalProcessById(String companyId);
	/**
	 * 
	 * @param companyId
	 * @return
	 */
	List<ApplicationCallExport> findByCom(String companyId);
	/**
	 * 
	 * @param companyId
	 * @return
	 */
	Optional<RestrictConfirmEmployment> getRestrictConfirmEmploymentByCompanyId(String companyId);
	/**
	 * 
	 * @param companyId
	 * @return
	 */
	List<RoleExport> findRole(String companyId);
}
