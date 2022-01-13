package nts.uk.ctx.sys.portal.dom.webmenu.webmenulinking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.sys.portal.dom.webmenu.WebMenuCode;
/**
 * 担当ロール別紐付け
 * @author tutk
 *
 */
@Getter
@AllArgsConstructor
public class RoleByRoleTies extends AggregateRoot  {
	
	/** ロールID	*/
	private String roleId;
	
	/** 会社ID	*/
	private String companyId;
	
	/** メニューコードリスト */
	private WebMenuCode webMenuCd;
	
}
