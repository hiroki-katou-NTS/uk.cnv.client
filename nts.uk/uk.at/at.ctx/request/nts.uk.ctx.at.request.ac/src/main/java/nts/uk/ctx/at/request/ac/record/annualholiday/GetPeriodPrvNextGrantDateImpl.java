package nts.uk.ctx.at.request.ac.record.annualholiday;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.GetPeriodFromPreviousToNextGrantDate;
import nts.uk.ctx.at.request.dom.application.annualholiday.GetPeriodPrvNextGrantDateAdapter;
import nts.uk.ctx.at.request.dom.application.annualholiday.GrantPeriodDto;
/**
 * 
 * @author phongtq
 *
 */
@Stateless
public class GetPeriodPrvNextGrantDateImpl implements GetPeriodPrvNextGrantDateAdapter{
	@Inject
	private GetPeriodFromPreviousToNextGrantDate date;

	@Override
	public Optional<GrantPeriodDto> getPeriodYMDGrant(String cid, String sid, GeneralDate ymd, Integer periodOutput,
			Optional<DatePeriod> fromTo) {
		Optional<GrantPeriodDto> result = date.getPeriodYMDGrant(cid, sid, ymd, periodOutput, fromTo).map(x -> {
			return new GrantPeriodDto(x.getPeriod(), x.getNextGrantDate());
		});
		return result;
	}

}
