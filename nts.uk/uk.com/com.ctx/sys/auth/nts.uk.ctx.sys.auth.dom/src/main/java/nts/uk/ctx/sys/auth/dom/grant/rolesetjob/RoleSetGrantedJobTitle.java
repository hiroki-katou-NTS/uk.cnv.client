package nts.uk.ctx.sys.auth.dom.grant.rolesetjob;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.dom.AggregateRoot;

/**
 * 
 * @author HungTT - ロールセット職位別付与
 *
 */

@Getter
@Setter
public class RoleSetGrantedJobTitle extends AggregateRoot {

	// 会社ID
	private String companyId;

	//兼務者にも適用する
	private boolean applyToConcurrentPerson;

	//ロールセット職位別付与詳細
	private List<RoleSetGrantedJobTitleDetail> details;

	public RoleSetGrantedJobTitle(String companyId, boolean applyToConcurrentPerson, List<RoleSetGrantedJobTitleDetail> details) {
		super();
		this.companyId = companyId;
		this.applyToConcurrentPerson = applyToConcurrentPerson;
		this.details = details;
	}
	
	public boolean isRoleSetCdExist(String roleSetCd){
		if (this.details == null || this.details.isEmpty()) return false;
		for (RoleSetGrantedJobTitleDetail d : this.details){
			if (d.getRoleSetCd().v().equals(roleSetCd)) {
				return true;
			}
		}
		return false;
	}
	
}
