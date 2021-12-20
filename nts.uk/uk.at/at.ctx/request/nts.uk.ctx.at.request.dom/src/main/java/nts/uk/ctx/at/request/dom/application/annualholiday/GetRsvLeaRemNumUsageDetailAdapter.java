package nts.uk.ctx.at.request.dom.application.annualholiday;

import java.util.List;

import nts.arc.time.calendar.period.DatePeriod;

public interface GetRsvLeaRemNumUsageDetailAdapter {
	public List<TmpReserveLeaveMngExport> getRsvLeaRemNumUsageDetail(String employeeId,DatePeriod period);
}
