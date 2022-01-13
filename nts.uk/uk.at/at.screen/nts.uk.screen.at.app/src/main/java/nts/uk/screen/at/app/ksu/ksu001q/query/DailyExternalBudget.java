package nts.uk.screen.at.app.ksu.ksu001q.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author thanhlv
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyExternalBudget {

	/** 職場グループ */
	private String unit;

	/** ID */
	private String id;

	/** 開始日 */
	private String startDate;

	/** 終了日 */
	private String endDate;

	/** 項目コード */
	private String itemCode;

}
