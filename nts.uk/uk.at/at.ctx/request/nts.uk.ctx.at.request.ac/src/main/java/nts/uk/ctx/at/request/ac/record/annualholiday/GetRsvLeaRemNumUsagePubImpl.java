package nts.uk.ctx.at.request.ac.record.annualholiday;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.pub.remainnumber.reserveleave.GetRsvLeaRemNumUsageDetailPub;
import nts.uk.ctx.at.request.dom.application.annualholiday.GetRsvLeaRemNumUsageDetailAdapter;
import nts.uk.ctx.at.request.dom.application.annualholiday.TmpReserveLeaveMngExport;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.CreateAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.UseDay;

@Stateless
public class GetRsvLeaRemNumUsagePubImpl implements GetRsvLeaRemNumUsageDetailAdapter{
	
	@Inject
	private GetRsvLeaRemNumUsageDetailPub pub;
	
	@Override
	public List<TmpReserveLeaveMngExport> getRsvLeaRemNumUsageDetail(String employeeId, DatePeriod period) {
		List<TmpReserveLeaveMngExport> result = pub.getRsvLeaRemNumUsageDetail(employeeId, period).stream().map(x -> {
			return new TmpReserveLeaveMngExport(x.getYmd(), x.getCreatorAtr(), new UseDay(x.getUseDays().v()));
		}).collect(Collectors.toList());
		
		return result;
	}
}
