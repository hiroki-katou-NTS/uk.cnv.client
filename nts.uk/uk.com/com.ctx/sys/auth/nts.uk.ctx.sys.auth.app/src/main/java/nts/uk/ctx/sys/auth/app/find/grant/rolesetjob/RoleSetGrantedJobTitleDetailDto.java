package nts.uk.ctx.sys.auth.app.find.grant.rolesetjob;

import lombok.Data;
import nts.uk.ctx.sys.auth.dom.grant.rolesetjob.RoleSetGrantedJobTitleDetail;

/**
 * 
 * @author HungTT
 *
 */

@Data
public class RoleSetGrantedJobTitleDetailDto {

	private String roleSetCd;
	
	private String jobTitleId;
	
	public static RoleSetGrantedJobTitleDetailDto fromDomain(RoleSetGrantedJobTitleDetail detail){
		return new RoleSetGrantedJobTitleDetailDto(detail.getRoleSetCd().v(), detail.getJobTitleId());
	}

	public RoleSetGrantedJobTitleDetailDto(String roleSetCd, String jobTitleId) {
		super();
		this.roleSetCd = roleSetCd;
		this.jobTitleId = jobTitleId;
	}
	
}
