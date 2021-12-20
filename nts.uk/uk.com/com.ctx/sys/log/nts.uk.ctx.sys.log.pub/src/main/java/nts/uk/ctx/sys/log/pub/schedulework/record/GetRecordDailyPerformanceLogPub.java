package nts.uk.ctx.sys.log.pub.schedulework.record;

import java.util.List;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.sys.log.pub.schedulework.CorrectRecordDailyResultExport;

/**
 * @author thanh_nx
 *
 *        日別実績の修正記録を取得するpublish
 */
public interface GetRecordDailyPerformanceLogPub {
	// [1] 項目IDを指定して取得する
	public List<CorrectRecordDailyResultExport> getBySpecifyItemId(String sid, GeneralDate targetDate, Integer itemId);
}
