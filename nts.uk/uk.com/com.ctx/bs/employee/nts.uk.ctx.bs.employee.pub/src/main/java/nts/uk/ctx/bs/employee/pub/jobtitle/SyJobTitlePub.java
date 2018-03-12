/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.bs.employee.pub.jobtitle;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;

/**
 * The Interface JobtitlePub.
 */
public interface SyJobTitlePub {

	/**
	 * Find job title by sid.
	 *
	 * @param employeeId the employee id
	 * @return the list
	 */
	// RequestList17
	List<JobTitleExport> findJobTitleBySid(String employeeId);

	/**
	 * Find job title by sid.
	 *
	 * @param employeeId the employee id
	 * @param baseDate the base date
	 * @return the list
	 */
	// RequestList33
	Optional<EmployeeJobHistExport> findBySid(String employeeId, GeneralDate baseDate);

	/**
	 * Find job title by position id.
	 *
	 * @param companyId the company id
	 * @param jobId the position id
	 * @param baseDate the base date
	 * @return the list
	 */
	// RequestList #67-1
	Optional<JobTitleExport> findByJobId(String companyId, String jobId, GeneralDate baseDate);

	/**
	 * Find by base date.
	 *
	 * @param companyId the company id
	 * @param baseDate the base date
	 * @return the list
	 */
	// RequestList #74
	List<JobTitleExport> findAll(String companyId, GeneralDate baseDate);

	/**
	 * Find S job hist by S id.
	 *
	 * @param employeeId the employee id
	 * @param baseDate the base date
	 * @return the list
	 */
	// RequestList #??? -> NamPT pls add on
	Optional<EmployeeJobHistExport> findSJobHistBySId(String employeeId, GeneralDate baseDate);

	/**
	 * Find by ids.
	 *
	 * @param companyId the company id
	 * @param jobIds the job ids
	 * @param baseDate the base date
	 * @return the list
	 */
	// RequestList #158
	List<SimpleJobTitleExport> findByIds(String companyId,List<String> jobIds, GeneralDate baseDate);
	
	/**
	 * Find job title by sid.
	 *
	 * @param employeeId the employee id
	 * @param baseDate the base date
	 * @return the list
	 */
	// RequestList From  LamDT
	Optional<AffJobTitleHistoryExport> gerBySidAndBaseDate(String employeeId, GeneralDate baseDate);

}
