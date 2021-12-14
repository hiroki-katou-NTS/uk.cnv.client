package nts.uk.ctx.sys.auth.dom.role.personrole;

import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;

/**
 * 個人情報ロール
 * 
 * @author dxthuong
 *
 */
@Getter
public class PersonRole extends AggregateRoot{
	
	/**
	 *  ロールID
	 */
	private String roleId;

	/**
	 * 未来日参照許可
	 */
	private Boolean referFutureDate;
	
	public PersonRole() {
		super();
	}


	public PersonRole(String roleId, Boolean referFutureDate) {
		super();
		this.roleId = roleId;
		this.referFutureDate = referFutureDate;
	}
	
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}


	public void setReferFutureDate(Boolean referFutureDate) {
		this.referFutureDate = referFutureDate;
	}

}
