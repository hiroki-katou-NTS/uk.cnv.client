package nts.uk.ctx.workflow.app.command.approvermanagement.setting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.workflow.dom.approvermanagement.setting.JobAssignSetting;

/**
 * 
 * @author yennth
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobAssignSettingCommand {
	/**
	 * 兼務者を含める
	 */
	private Boolean isConcurrently;
	
	public JobAssignSetting toDomain(String companyId){
		JobAssignSetting job = JobAssignSetting.createFromJavaType(companyId, this.isConcurrently);
		return job;
	}
}
