package nts.uk.ctx.sys.portal.app.screenquery.topppagepart.createflowmenu;

import lombok.Builder;
import lombok.Data;

/**
 * フローメニューレイアウトのメニュー設定
 */
@Data
@Builder
public class MenuSettingDto {
	
	/**
	 * 会社ID									
	 */
	private String cid;
	
	/**
	 * フローメニューコード									
	 */
	private String flowMenuCode;
	
	/**
	 * column																
	 */
	private int column;
	
	/**
	 * row									
	 */
	private int row;
	
	/**
	 * メニュー名称																	
	 */
	private String menuName;
	
	/**
	 * メニューコード																		
	 */
	private String menuCode;
	
	/**
	 * メニュー分類
	 */
	private int menuClassification;
	
	/**
	 * システム区分
	 */
	private int systemType;
	
	/**
	 * width
	 */
	private int width;
	
	/**
	 * height
	 */
	private int height;
	
	/**
	 * 文字のサイズ									
	 */
	private int fontSize;
	
	/**
	 * 太字
	 */
	private int bold;

	/**
	 * 横の位置
	 */
	private int horizontalPosition;
	
	/**
	 * 縦の位置
	 */
	private int verticalPosition;
	
	/**
	 * 文字の色
	 */
	private String textColor;
	
	/**
	 * 既定区分
	 */
	private Integer isFixed;
	
	/**
	 * 比率
	 */
	private Double ratio;
	
	/**
	 * 画像ファイルID
	 */
	private String fileId;
	
	/**
	 * 画像ファイル名
	 */
	private String fileName;
}
