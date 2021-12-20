package nts.uk.ctx.at.record.pubimp.remainnumber.reserveleave;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.remainingnumber.reserveleave.export.GetRsvLeaRemNumUsageDetail;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.record.pub.remainnumber.reserveleave.GetRsvLeaRemNumUsageDetailPub;
import nts.uk.ctx.at.record.pub.remainnumber.reserveleave.TmpReserveLeaveMngExport;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.UseDay;
import nts.uk.ctx.at.shared.dom.remainingnumber.reserveleave.interim.TmpResereLeaveMng;
@Stateless
public class GetRsvLeaRemNumUsageDetailPubImpl implements GetRsvLeaRemNumUsageDetailPub{

	@Inject
	private RecordDomRequireService requireService;
	
	@Override
	public List<TmpReserveLeaveMngExport> getRsvLeaRemNumUsageDetail(String employeeId, DatePeriod period) {
		val require = requireService.createRequire();
		
		List<TmpResereLeaveMng> rsvGrantRemainingDatas = GetRsvLeaRemNumUsageDetail.getRsvLeaRemNumUsageDetail(
				employeeId, period, require);
		
		List<TmpReserveLeaveMngExport> tmpManageList = new ArrayList<>();
		tmpManageList.addAll(
				rsvGrantRemainingDatas.stream()
				.map(x -> new TmpReserveLeaveMngExport(
				x.getYmd(), 
				x.getCreatorAtr(),
				new UseDay(x.getUseDays().v())))
				.collect(Collectors.toList()));
			
		
		return tmpManageList;
	}

}
