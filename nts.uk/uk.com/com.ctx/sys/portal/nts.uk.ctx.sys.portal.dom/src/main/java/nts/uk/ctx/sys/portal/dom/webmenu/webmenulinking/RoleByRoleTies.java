package nts.uk.ctx.sys.portal.dom.webmenu.webmenulinking;

import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.sys.portal.dom.webmenu.WebMenuCode;
/**
 * 担当ロール別紐付け
 * @author tutk
 *
 */
@Getter
public class RoleByRoleTies extends AggregateRoot  {
    /**ロールID */
    private String roleId;
	/** メニューコードリスト */
    private WebMenuCode webMenuCd;
    
    private String companyId;

	public RoleByRoleTies(String roleId,WebMenuCode webMenuCd, String companyId) {
		super();
		this.roleId = roleId;
		this.webMenuCd = webMenuCd;
		this.companyId = companyId;
	}
}
