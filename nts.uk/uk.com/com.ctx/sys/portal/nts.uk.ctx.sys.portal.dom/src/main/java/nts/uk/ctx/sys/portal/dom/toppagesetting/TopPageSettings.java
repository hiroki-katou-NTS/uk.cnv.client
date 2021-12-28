package nts.uk.ctx.sys.portal.dom.toppagesetting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.layer.dom.DomainObject;

/**
 * The Class TopPageSettings.
 * DomainObject トップページ設定
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TopPageSettings extends DomainObject {

	/** 
	 * The top menu code.
	 * 	トップメニューコード
	 **/
	protected TopMenuCode topMenuCode;

	/** 
	 * The menu login. 
	 * 	ログインメニュー 
	 **/
	protected MenuLogin menuLogin;
	
	public TopPageSettings getTopPageSettings() {
		return new TopPageSettings(
				this.getTopMenuCode(), 
				this.getMenuLogin());
	}
}
