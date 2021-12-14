package nts.uk.ctx.at.shared.infra.repository.remainingnumber;

import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.AnnualLeaveMaxHistRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.AnnualLeaveMaxHistoryData;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.HalfdayAnnualLeaveMax;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.MaxMinutes;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.MaxTimes;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.RemainingMinutes;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.RemainingTimes;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.TimeAnnualLeaveMax;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.UsedMinutes;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.UsedTimes;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedTime;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualNumberDay;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.ctx.at.shared.infra.entity.remainingnumber.annlea.KrcdtAnnLeaMaxHist;
import nts.uk.ctx.at.shared.infra.entity.remainingnumber.annlea.KrcdtAnnLeaMaxHistPK;
import nts.uk.ctx.at.shared.infra.entity.worktype.KshmtWorkType;
import nts.uk.ctx.at.shared.infra.entity.worktype.KshmtWorkTypePK;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

/**
 *
 * @author HungTT
 *
 */

@Stateless
public class JpaAnnualLeaveMaxHistRepository extends JpaRepository implements AnnualLeaveMaxHistRepository {

	@Override
	public void addOrUpdate(AnnualLeaveMaxHistoryData domain) {

		Optional<AnnualLeaveMaxHistoryData> obj = this.find(
				domain.getEmployeeId(), domain.getYearMonth(), domain.getClosureId(), domain.getClosureDate());

		Optional<KrcdtAnnLeaMaxHist> opt = Optional.empty();
		if(obj.isPresent())
			opt = Optional.of(this.toEntity(obj.get()));

		if (opt.isPresent()) {
			KrcdtAnnLeaMaxHist entity = opt.get();
			if (domain.getHalfdayAnnualLeaveMax().isPresent()) {
				entity.maxTimes = domain.getHalfdayAnnualLeaveMax().get().getMaxTimes().v();
				entity.usedTimes = domain.getHalfdayAnnualLeaveMax().get().getUsedTimes().v();
				entity.remainingTimes = domain.getHalfdayAnnualLeaveMax().get().getRemainingTimes().v();
			}
			if (domain.getTimeAnnualLeaveMax().isPresent()) {
				entity.maxMinutes = domain.getTimeAnnualLeaveMax().get().getMaxMinutes().v();
				entity.usedMinutes = domain.getTimeAnnualLeaveMax().get().getUsedMinutes().v();
				entity.remainingMinutes = domain.getTimeAnnualLeaveMax().get().getRemainingMinutes().v();
			}
			this.commandProxy().update(entity);
		} else {
			this.commandProxy().insert(KrcdtAnnLeaMaxHist.fromDomain(domain));
		}
	}

	public Optional<AnnualLeaveMaxHistoryData> find(
			String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate){

		KrcdtAnnLeaMaxHistPK pk =new KrcdtAnnLeaMaxHistPK(
				employeeId,
				yearMonth.v(),
				closureId.value,
				closureDate.getClosureDay().v(),
				closureDate.getLastDayOfMonth()?1:0);

		Optional<KrcdtAnnLeaMaxHist> data = this.queryProxy().find(pk, KrcdtAnnLeaMaxHist.class);
		if (data.isPresent()) {
			return Optional.of(toDomain(data.get()));
		}
		return Optional.empty();
	}

	private KrcdtAnnLeaMaxHist toEntity(AnnualLeaveMaxHistoryData domain) {
		return new KrcdtAnnLeaMaxHist(
				domain.getEmployeeId(),
				domain.getHalfdayAnnualLeaveMax().map(c -> c.getMaxTimes().v()).orElse(null),
				domain.getHalfdayAnnualLeaveMax().map(c -> c.getUsedTimes().v()).orElse(null),
				domain.getHalfdayAnnualLeaveMax().map(c -> c.getRemainingTimes().v()).orElse(null),
				domain.getTimeAnnualLeaveMax().map(c -> c.getMaxMinutes().v()).orElse(null),
				domain.getTimeAnnualLeaveMax().map(c -> c.getUsedMinutes().v()).orElse(null),
				domain.getTimeAnnualLeaveMax().map(c -> c.getRemainingMinutes().v()).orElse(null),
				domain.getYearMonth().v(),
				domain.getClosureId().value,
				domain.getClosureDate().getClosureDay().v(),
				domain.getClosureDate().getLastDayOfMonth()?1:0);
	}

	private AnnualLeaveMaxHistoryData toDomain(KrcdtAnnLeaMaxHist entity){

		Optional<HalfdayAnnualLeaveMax> halfdayAnnualLeaveMax = Optional.empty();
		if ( entity.maxTimes != null && entity.usedTimes != null && entity.remainingTimes != null ) {
			halfdayAnnualLeaveMax = Optional.of(new HalfdayAnnualLeaveMax(
					new AnnualNumberDay(entity.maxTimes),
					new UsedTimes(entity.usedTimes),
					new RemainingTimes(entity.remainingTimes)));
		}

		Optional<TimeAnnualLeaveMax> timeAnnualLeaveMax = Optional.empty();
		if ( entity.maxMinutes != null && entity.usedMinutes != null && entity.remainingMinutes != null ) {
			timeAnnualLeaveMax = Optional.of(new TimeAnnualLeaveMax(
					new MaxMinutes(entity.maxMinutes),
					new LeaveUsedTime(entity.usedMinutes),
					new LeaveRemainingTime(entity.remainingMinutes)));
		}

		return new AnnualLeaveMaxHistoryData(
				entity.PK.sid,
				halfdayAnnualLeaveMax,
				timeAnnualLeaveMax,
				new YearMonth(entity.PK.yearMonth),
				EnumAdaptor.valueOf(entity.PK.closureId, ClosureId.class),
				new ClosureDate(entity.PK.closeDay, entity.PK.isLastDay==1)
				);

	}

}
