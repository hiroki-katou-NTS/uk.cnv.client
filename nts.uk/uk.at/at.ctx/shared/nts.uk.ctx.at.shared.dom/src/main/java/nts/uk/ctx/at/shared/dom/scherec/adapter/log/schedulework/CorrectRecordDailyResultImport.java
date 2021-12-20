package nts.uk.ctx.at.shared.dom.scherec.adapter.log.schedulework;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;

/**
 * @author thanh_nx
 *
 *         日別実績の修正記録
 * 
 *         勤務予定の修正記録
 */
@AllArgsConstructor
@Getter
public class CorrectRecordDailyResultImport {

	// 社員ID
	private String sid;

	// 対象年月日
	private GeneralDate targetDate;

	// 項目ID
	private Integer itemId;

	// 修正時刻
	private GeneralDateTime correctTime;
}
