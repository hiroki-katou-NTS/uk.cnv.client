package nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu;

import java.util.Optional;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.ポータル.トップページの部品.フローメニュー作成.画像情報
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageInformation {

	/**
	 * 既定区分
	 */
	private FixedClassification isFixed;

	/**
	 * 比率
	 */
	private double ratio;

	/**
	 * 画像ファイルID
	 */
	private Optional<String> fileId;

	/**
	 * 画像ファイル名
	 */
	private Optional<FileName> fileName;

	public static ImageInformation createFromJavatype(Integer isFixed, Double ratio, String fileId, String fileName) {
		FixedClassification fixedCls = FixedClassification.valueOf(isFixed);
		if (fixedCls == null) {
			return null;
		}
		return new ImageInformation(fixedCls, ratio, Optional.ofNullable(fileId),
				Optional.ofNullable(fileName).map(FileName::new));
	}
}
