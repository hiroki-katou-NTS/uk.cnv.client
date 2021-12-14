package nts.uk.ctx.at.shared.dom.remainingnumber.work.service;

import java.util.List;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.ScheRemainCreateInfor;

public interface RemainCreateInforByScheData {
	
	/**
	 * 暫定データを作成する為の勤務予定を取得する
	 * @param cid
	 * @param sid
	 * @param ymd 年月日
	 * @return
	 */
	List<ScheRemainCreateInfor> createRemainInforNew(String cid, String sid, List<GeneralDate> dates);
}
