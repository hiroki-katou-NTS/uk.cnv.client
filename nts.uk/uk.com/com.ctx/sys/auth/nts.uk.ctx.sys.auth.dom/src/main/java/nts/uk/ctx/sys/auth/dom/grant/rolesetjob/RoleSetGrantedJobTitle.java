package nts.uk.ctx.sys.auth.dom.grant.rolesetjob;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.dom.objecttype.DomainAggregate;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSetCode;

/**
 * ロールセット職位別付与
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.権限管理.付与.ロールセット職位別付与.ロールセット職位別付与
 * @author HungTT
 */
@Getter
@Setter
@AllArgsConstructor
public class RoleSetGrantedJobTitle implements DomainAggregate {
	
	/**
	 * 会社ID
	 */
	private final String companyId;

	/**
	 * 職位ID
	 */
	private final String jobTitleId;
	
	/**
	 * ロールセットコード
	 */
	private RoleSetCode roleSetCd;

}
