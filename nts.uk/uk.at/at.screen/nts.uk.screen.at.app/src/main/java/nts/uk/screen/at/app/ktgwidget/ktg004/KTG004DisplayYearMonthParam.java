package nts.uk.screen.at.app.ktgwidget.ktg004;

import lombok.Value;
import nts.arc.time.GeneralDate;

/**
 * UKDesign.UniversalK.就業.KTG_ウィジェット.KTG004_勤務状況.パラメータ.KTG004表示年月
 */
@Value
public class KTG004DisplayYearMonthParam {

	/**
	 * 表示年月
	 */
	private Integer displayYearMonth;
	
	/**
	 * 翌月処理月
	 */
	private Integer nextProcessingYm;
	
	/**
	 * 当月処理月
	 */
	private Integer currentProcessingYm;
	
	/**
	 * 翌月処理開始日
	 */
	private GeneralDate nextProcessingYmStartDate;
	
	/**
	 * 翌月処理終了日
	 */
	private GeneralDate nextProcessingYmEndDate;
	
	/**
	 * 当月処理開始日
	 */
	private GeneralDate currentProcessingYmStartDate;
	
	/**
	 * 当月処理終了日
	 */
	private GeneralDate currentProcessingYmEndDate;
	
	/**
	 * 締めID
	 */
	private Integer closureId;
}
