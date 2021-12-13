package nts.uk.ctx.at.shared.dom.scherec.adapter.log.schedulework.schedule;

import java.util.List;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.adapter.log.schedulework.CorrectRecordDailyResultImport;

/**
 * @author thanh_nx
 * 
 *         勤務予定の修正記録を取得するAdapter
 * 
 */
public interface GetWorkScheduleLogAdapter {
	// [1] 項目IDを指定して取得する
	public List<CorrectRecordDailyResultImport> getBySpecifyItemId(String sid, GeneralDate targetDate, Integer itemId);
}
