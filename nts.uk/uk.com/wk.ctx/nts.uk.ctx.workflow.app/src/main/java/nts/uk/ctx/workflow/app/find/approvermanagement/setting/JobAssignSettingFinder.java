package nts.uk.ctx.workflow.app.find.approvermanagement.setting;

import java.util.Optional;

import javax.ejb.Stateless;
/**
 * @author yennth
 */
import javax.inject.Inject;

import nts.uk.ctx.workflow.dom.approvermanagement.setting.JobAssignSetting;
import nts.uk.ctx.workflow.dom.approvermanagement.setting.JobAssignSettingRepository;
import nts.uk.shr.com.context.AppContexts;
@Stateless
public class JobAssignSettingFinder {
	@Inject
	private JobAssignSettingRepository jobRep;
	
	public JobAssignSettingDto findApp(){
		String companyId = AppContexts.user().companyId();
		Optional<JobAssignSetting> jobAssign = jobRep.findById();
		if(jobAssign.isPresent()){
			return new JobAssignSettingDto(companyId, jobAssign.get().getIsConcurrently());
		}
		return null;
	}
}
