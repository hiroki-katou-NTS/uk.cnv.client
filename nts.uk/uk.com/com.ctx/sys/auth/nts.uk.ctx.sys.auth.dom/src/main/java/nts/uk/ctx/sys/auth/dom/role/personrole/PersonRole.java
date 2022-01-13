package nts.uk.ctx.sys.auth.dom.role.personrole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.dom.AggregateRoot;

/**
 * 個人情報ロール
 * 
 * @author dxthuong
 *
 */
@Getter
@AllArgsConstructor
public class PersonRole extends AggregateRoot{
	
	/**
	 *  ロールID
	 */
	private String roleId;
	
	/**
	 * 会社ID
	 */
	private String companyId;

	/**
	 * 未来日参照許可
	 */
	@Setter
	private Boolean referFutureDate;

}
