package nts.uk.ctx.at.record.pubimp.remainnumber.annualleave;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.FindAnnLeaUsedDaysFromPreviousToNextGrantDate;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.record.pub.remainnumber.annualleave.FindAnnLeaUsedDaysFromPreviousToNextGrantDatePub;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.daynumber.AnnualLeaveUsedDayNumber;


@Stateless
public class FindAnnLeaUsedDaysFromPreviousToNextGrantDateImpl implements FindAnnLeaUsedDaysFromPreviousToNextGrantDatePub{
	
	@Inject
	private RecordDomRequireService requireService;

	@Override
	public AnnualLeaveUsedDayNumber findUsedDays(String employeeId, GeneralDate criteriaDate) {
		val require = requireService.createRequire();
		val cacheCarrier = new CacheCarrier();
		
		
		return FindAnnLeaUsedDaysFromPreviousToNextGrantDate.findUsedDays( employeeId,  criteriaDate,
				 require,  cacheCarrier);
	}

}
