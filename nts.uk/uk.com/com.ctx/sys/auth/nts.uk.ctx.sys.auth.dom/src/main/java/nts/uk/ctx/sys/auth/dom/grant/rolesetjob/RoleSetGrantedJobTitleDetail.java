package nts.uk.ctx.sys.auth.dom.grant.rolesetjob;

import lombok.Getter;
import nts.arc.layer.dom.DomainObject;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSetCode;

/**
 * 
 * @author HungTT - ロールセット職位別付与詳細
 *
 */

@Getter
public class RoleSetGrantedJobTitleDetail extends DomainObject {

	// ロールセットコード.
	private RoleSetCode roleSetCd;

	// 職位ID
	private String jobTitleId;

	//会社ID
	private String companyId;
	
	public RoleSetGrantedJobTitleDetail(String roleSetCd, String jobTitleId, String companyId) {
		super();
		this.roleSetCd = new RoleSetCode(roleSetCd);
		this.jobTitleId = jobTitleId;
		this.companyId = companyId;
	}
	
}
