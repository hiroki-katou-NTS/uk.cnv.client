package nts.uk.ctx.workflow.dom.approvermanagement.setting;

import java.util.Optional;

public interface JobAssignSettingRepository {
	/**
	 * finById by companyId
	 * @param companyId
	 * @return
	 */
	Optional<JobAssignSetting> findById();
	/**
	 * update job assign setting
	 * @param jobAssign
	 * @author yennth
	 */
	void updateJob(JobAssignSetting jobAssign);
	/**
	 * insert job assign setting
	 * @param jobAssign
	 * @author yennth
	 */
	void insertJob(JobAssignSetting jobAssign);
}
