package nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported;

import java.util.List;
import java.util.Optional;

import nts.arc.time.calendar.period.DatePeriod;

/**
 * 社員の所属会社履歴Adapter
 * @author HieuLt
 *
 */
public interface EmpComHisAdapter {

	/**[1] 期間を指定して在籍期間を取得する **/
	List<EmpEnrollPeriodImport> getEnrollmentPeriod (List<String> lstEmpId , DatePeriod datePeriod);
	/**[2] 期間を指定して最新の在籍期間を取得する **/
	Optional<EmpEnrollPeriodImport> getLatestEnrollmentPeriod(String lstEmpId , DatePeriod datePeriod);
}
