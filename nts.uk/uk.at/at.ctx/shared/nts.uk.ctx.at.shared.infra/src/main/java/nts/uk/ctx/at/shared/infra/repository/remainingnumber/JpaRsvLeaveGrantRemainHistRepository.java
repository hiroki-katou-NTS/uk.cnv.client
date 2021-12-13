package nts.uk.ctx.at.shared.infra.repository.remainingnumber;

import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.reserveleave.empinfo.grantremainingdata.ReserveLeaveGrantRemainHistoryData;
import nts.uk.ctx.at.shared.dom.remainingnumber.reserveleave.empinfo.grantremainingdata.RsvLeaveGrantRemainHistRepository;
import nts.uk.ctx.at.shared.infra.entity.remainingnumber.resvlea.empinfo.grantremainingdata.KrcdtReserveLeaveRemainHist;
import nts.uk.ctx.at.shared.infra.entity.remainingnumber.resvlea.empinfo.grantremainingdata.KrcdtReserveLeaveRemainHistPK;

/**
 *
 * @author HungTT
 *
 */

@Stateless
public class JpaRsvLeaveGrantRemainHistRepository extends JpaRepository implements RsvLeaveGrantRemainHistRepository {

	@Override
	public void addOrUpdate(ReserveLeaveGrantRemainHistoryData domain, String cid) {
		KrcdtReserveLeaveRemainHistPK krcdtReserveLeaveRemainHistPK = new KrcdtReserveLeaveRemainHistPK(domain.getEmployeeId(),
				domain.getYearMonth().v(), domain.getClosureId().value, domain.getClosureDate().getClosureDay().v(),
				domain.getClosureDate().getLastDayOfMonth(), domain.getGrantDate());
		Optional<KrcdtReserveLeaveRemainHist> entityOpt = this.queryProxy().find(krcdtReserveLeaveRemainHistPK,
				KrcdtReserveLeaveRemainHist.class);
		if (entityOpt.isPresent()) {
			KrcdtReserveLeaveRemainHist entity = entityOpt.get();
//			entity.cid = cid;
			entity.deadline = domain.getDeadline();
			entity.expStatus = domain.getExpirationStatus().value;
			entity.registerType = domain.getRegisterType().value;
			entity.grantDays = domain.getDetails().getGrantNumber().getDays().v();
			entity.usedDays = domain.getDetails().getUsedNumber().getDays().v();
			entity.overLimitDays = domain.getDetails().getUsedNumber().getLeaveOverLimitNumber().isPresent()
					? domain.getDetails().getUsedNumber().getLeaveOverLimitNumber().get().numberOverDays.v() : null;
			entity.remainingDays = domain.getDetails().getRemainingNumber().getDays().v();
			this.commandProxy().update(entity);
		} else
			this.commandProxy().insert(KrcdtReserveLeaveRemainHist.fromDomain(domain));
	}
}
