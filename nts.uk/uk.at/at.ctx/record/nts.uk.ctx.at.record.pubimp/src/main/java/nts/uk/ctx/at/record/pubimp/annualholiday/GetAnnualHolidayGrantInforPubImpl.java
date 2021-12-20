package nts.uk.ctx.at.record.pubimp.annualholiday;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.GetAnnualHolidayGrantInfor;
import nts.uk.ctx.at.record.pub.annualholiday.GetAnnualHolidayGrantInforPub;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.param.DailyInterimRemainMngDataAndFlg;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.param.ReferenceAtr;


@Stateless
public class GetAnnualHolidayGrantInforPubImpl implements GetAnnualHolidayGrantInforPub{
	@Inject
	private GetAnnualHolidayGrantInfor grantInfor;
	
	@Override
	public List<DailyInterimRemainMngDataAndFlg> lstRemainData(String cid, String sid, DatePeriod datePeriod,
			ReferenceAtr referenceAtr) {
		List<DailyInterimRemainMngDataAndFlg> result = grantInfor.lstRemainData(cid, sid, datePeriod, referenceAtr);
		return result;
	}
}
