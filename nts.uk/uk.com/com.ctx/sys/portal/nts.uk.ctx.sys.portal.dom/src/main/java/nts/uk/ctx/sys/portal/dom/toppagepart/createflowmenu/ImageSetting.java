package nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import nts.arc.layer.dom.DomainObject;

/**
 *UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.ポータル.トップページの部品.フローメニュー作成.画像設定
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
public class ImageSetting extends DomainObject {
	
	/**
	 * サイズと位置
	 */
	private SizeAndPosition sizeAndPosition;
	
	/**
	 * 画像情報
	 */
	private ImageInformation imageInformation;
}
