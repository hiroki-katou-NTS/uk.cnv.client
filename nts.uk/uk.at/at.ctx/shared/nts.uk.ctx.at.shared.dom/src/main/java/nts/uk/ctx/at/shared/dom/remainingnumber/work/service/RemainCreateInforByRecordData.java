package nts.uk.ctx.at.shared.dom.remainingnumber.work.service;

import java.util.List;

import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.RecordRemainCreateInfor;

public interface RemainCreateInforByRecordData {
	/**
	 * 残数作成元の勤務実績を取得する
	 * @param cid
	 * @param sid
	 * @param dateData リスト
	 * @return
	 */
	public List<RecordRemainCreateInfor> lstRecordRemainData(CacheCarrier cacheCarrier, String cid, String sid, List<GeneralDate> dateData);

}
