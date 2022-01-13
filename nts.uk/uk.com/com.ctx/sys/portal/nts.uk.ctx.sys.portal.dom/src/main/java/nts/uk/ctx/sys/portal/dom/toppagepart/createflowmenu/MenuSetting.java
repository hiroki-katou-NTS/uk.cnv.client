package nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu;

import java.util.Optional;

import lombok.Builder;
import lombok.Getter;
import nts.arc.layer.dom.DomainObject;
import nts.uk.ctx.sys.portal.dom.webmenu.MenuCode;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.ポータル.トップページの部品.フローメニュー作成.メニュー設定
 */
@Getter
@Builder
public class MenuSetting extends DomainObject {
	
	/**
	 * サイズと位置
	 */
	private SizeAndPosition sizeAndPosition;
	
	/**
	 * システム区分
	 */
	private System systemType;
	
	/**
	 * メニューコード
	 */
	private MenuCode menuCode;
	
	/**
	 * メニュー分類
	 */
	private MenuClassification menuClassification;
	
	/**
	 * 文字の設定
	 */
	private FontSetting fontSetting;
	
	/**
	 * メニュー名称
	 */
	private DisplayName menuName;
	
	/**
	 * 画像情報
	 */
	private Optional<ImageInformation> imageInformation;
}
