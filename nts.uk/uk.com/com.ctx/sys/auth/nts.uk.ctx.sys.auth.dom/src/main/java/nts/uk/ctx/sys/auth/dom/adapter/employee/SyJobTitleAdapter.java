package nts.uk.ctx.sys.auth.dom.adapter.employee;

import java.util.List;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.sys.auth.dom.employee.dto.JobTitleImport;

public interface SyJobTitleAdapter {
	/**
	 * Find job title by sid.
	 *
	 * @param employeeId
	 *            the employee id
	 * @return the list
	 */
	// RequestList #17
	List<JobTitleImport> findJobTitleBySid(String employeeId);

	/**
	 * Find job title by sid.
	 *
	 * @param employeeId
	 *            the employee id
	 * @param baseDate
	 *            the base date
	 * @return the list
	 */
	// RequestList #33
	JobTitleImport findJobTitleBySid(String employeeId, GeneralDate baseDate);

	/**
	 * Find job title by position id.
	 *
	 * @param companyId
	 *            the company id
	 * @param positionId
	 *            the position id
	 * @param baseDate
	 *            the base date
	 * @return the list
	 */
	// RequestList #67-1
	JobTitleImport findJobTitleByPositionId(String companyId, String positionId, GeneralDate baseDate);

	/**
	 * Find by base date.
	 *
	 * @param companyId
	 *            the company id
	 * @param baseDate
	 *            the base date
	 * @return the list
	 */
	// RequestList #74
	List<JobTitleImport> findAll(String companyId, GeneralDate baseDate);
}
