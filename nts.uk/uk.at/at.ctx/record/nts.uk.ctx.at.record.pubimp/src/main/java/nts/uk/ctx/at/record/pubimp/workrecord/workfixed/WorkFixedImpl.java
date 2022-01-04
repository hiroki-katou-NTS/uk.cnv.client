package nts.uk.ctx.at.record.pubimp.workrecord.workfixed;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.workrecord.workrecord.EmploymentConfirmed;
import nts.uk.ctx.at.record.dom.workrecord.workrecord.EmploymentConfirmedRepository;
import nts.uk.ctx.at.record.pub.workrecord.workfixed.WorkFixedPub;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosurePeriod;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;

@Stateless
public class WorkFixedImpl implements WorkFixedPub{
	@Inject
	private ClosureRepository closureRepository;
	@Inject
	private EmploymentConfirmedRepository workfixedRepository;
	
	@Override
	public boolean getEmploymentFixedStatus(String companyID, GeneralDate date, String workPlaceID, int closureID) {
		// ドメインモデル「締め」を取得する
		Optional<Closure> optionalClosure = closureRepository.findById(companyID, closureID);
		if(!optionalClosure.isPresent()){
			return false;
		}
		Closure closure = optionalClosure.get();
		Optional<ClosurePeriod> optionalClosurePeriod = closure.getClosurePeriodByYmd(date);
		if(!optionalClosurePeriod.isPresent()){
			return false;
		}
		Optional<EmploymentConfirmed> workFixed = this.workfixedRepository.get(companyID, workPlaceID, ClosureId.valueOf(closureID), optionalClosurePeriod.get().getYearMonth());
		if(!workFixed.isPresent()){
			return false;
		}
//		if(workFixed.get().getConfirmClsStatus().equals(ConfirmClsStatus.Confirm)){
//			return true;
//		}
		return true;
	}

}
