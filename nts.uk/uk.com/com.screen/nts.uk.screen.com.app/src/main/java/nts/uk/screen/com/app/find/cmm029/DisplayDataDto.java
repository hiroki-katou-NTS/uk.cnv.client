package nts.uk.screen.com.app.find.cmm029;

import lombok.Builder;
import lombok.Data;

/**
 * UKDesign.UniversalK.共通.CMM_マスタメンテナンス.CMM029_機能の選択.A : 機能の選択.パラメータ.機能の選択の表示データDTO
 */
@Data
@Builder
public class DisplayDataDto {

	/**
	 * システム
	 */
	private Integer system;
	
	/**
	 * プログラムID
	 */
	private String programId;
	
	/**
	 * URL
	 */
	private String url;
	
	/**
	 * エラーメッセージ
	 */
	private String errorMessage;
	
	/**
	 * 作業運用方法
	 */
	private int taskOperationMethod;
	
	/**
	 * 利用区分
	 */
	private boolean useAtr;
}
