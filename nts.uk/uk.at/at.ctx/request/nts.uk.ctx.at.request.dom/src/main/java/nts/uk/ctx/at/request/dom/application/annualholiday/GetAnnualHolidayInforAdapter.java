package nts.uk.ctx.at.request.dom.application.annualholiday;

import java.util.List;

import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.param.DailyInterimRemainMngDataAndFlg;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.param.ReferenceAtr;

public interface GetAnnualHolidayInforAdapter {
	/**
	 * [NO.551]期間内の年休使用明細を取得する
	 * 
	 * @param cid
	 * @param sid
	 * @param datePeriod
	 * @param referenceAtr
	 * @return
	 */
	List<DailyInterimRemainMngDataAndFlg> lstRemainData(String cid, String sid, DatePeriod datePeriod,
			ReferenceAtr referenceAtr);
}
