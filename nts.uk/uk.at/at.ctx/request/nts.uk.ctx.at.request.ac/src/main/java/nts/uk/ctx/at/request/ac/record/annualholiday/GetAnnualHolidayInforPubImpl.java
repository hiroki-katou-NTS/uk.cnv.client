package nts.uk.ctx.at.request.ac.record.annualholiday;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.pub.annualholiday.GetAnnualHolidayGrantInforPub;
import nts.uk.ctx.at.request.dom.application.annualholiday.GetAnnualHolidayInforAdapter;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.param.DailyInterimRemainMngDataAndFlg;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.param.ReferenceAtr;

@Stateless
public class GetAnnualHolidayInforPubImpl implements GetAnnualHolidayInforAdapter{

	@Inject
	private GetAnnualHolidayGrantInforPub pub;
	
	@Override
	public List<DailyInterimRemainMngDataAndFlg> lstRemainData(String cid, String sid, DatePeriod datePeriod,
			ReferenceAtr referenceAtr) {
		List<DailyInterimRemainMngDataAndFlg> result = pub.lstRemainData(cid, sid, datePeriod, referenceAtr);
		return result;
	}
}
