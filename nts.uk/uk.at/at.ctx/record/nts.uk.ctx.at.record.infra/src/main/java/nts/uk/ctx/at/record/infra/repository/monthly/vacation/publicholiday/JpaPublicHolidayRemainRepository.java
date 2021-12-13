package nts.uk.ctx.at.record.infra.repository.monthly.vacation.publicholiday;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.remainmerge.RemainMergeRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.publicholiday.PublicHolidayRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.publicholiday.PublicHolidayRemNumEachMonthRepository;

@Stateless
public class JpaPublicHolidayRemainRepository extends JpaRepository implements PublicHolidayRemNumEachMonthRepository {

	@Inject
	private RemainMergeRepository remainRepo;
	
	@Override
	public void persistAndUpdate(PublicHolidayRemNumEachMonth domain) {
		this.remainRepo.persistAndUpdate(domain);
	}

}
