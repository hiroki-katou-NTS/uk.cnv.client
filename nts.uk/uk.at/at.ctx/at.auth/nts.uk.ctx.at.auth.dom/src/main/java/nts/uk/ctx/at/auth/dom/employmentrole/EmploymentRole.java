package nts.uk.ctx.at.auth.dom.employmentrole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.shr.com.enumcommon.NotUseAtr;
/**
 * 就業ロール
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.権限管理.就業ロール.就業ロール
 */
@Getter
@AllArgsConstructor
public class EmploymentRole extends AggregateRoot {
	/**	ロールID	*/
	private String roleId;
	
	/**	会社ID	*/
	private String companyId;

	/**	未来日参照許可	*/
	private NotUseAtr futureDateRefPermit;
	/**
	 * 作る
	 * @param roleId ロールID
	 * @param companyId 会社ID
	 * @return 就業ロール
	 */
	public static EmploymentRole createEmploymentRole(String roleId, String companyId) {
		return new EmploymentRole(roleId, companyId, NotUseAtr.NOT_USE); 
	}

}
