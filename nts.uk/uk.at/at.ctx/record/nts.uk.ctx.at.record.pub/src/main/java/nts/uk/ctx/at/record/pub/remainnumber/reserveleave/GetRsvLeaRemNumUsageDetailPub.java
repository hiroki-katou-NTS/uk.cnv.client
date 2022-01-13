package nts.uk.ctx.at.record.pub.remainnumber.reserveleave;

import java.util.List;

import nts.arc.time.calendar.period.DatePeriod;

public interface GetRsvLeaRemNumUsageDetailPub {

	public List<TmpReserveLeaveMngExport> getRsvLeaRemNumUsageDetail(String employeeId,DatePeriod period);
}
