package nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.dto;

import lombok.Builder;
import lombok.Getter;

/**
 *  フローメニューレイアウトの画像設定
 */
@Getter
@Builder
public class ImageSettingDto {
	
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
	 * 画像ファイルID																								
	 */
	private String fileId;
	
	/**
	 * 画像ファイル名																	
	 */
	private String fileName;
	
	/**
	 * 既定区分
	 */
	private int isFixed;
	
	/**
	 * width
	 */
	private int width;
	
	/**
	 * height
	 */
	private int height;
	
	/**
	 * 比率
	 */
	private double ratio;
}
