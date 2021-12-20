package nts.uk.ctx.at.shared.infra.repository.remainingnumber;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnualLeaveRemainHistRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnualLeaveRemainingHistory;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.LeaveExpirationStatus;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.ctx.at.shared.infra.entity.remainingnumber.annlea.KrcdtAnnLeaRemainHist;
import nts.uk.ctx.at.shared.infra.entity.remainingnumber.annlea.KrcdtAnnLeaRemainHistPK;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

/**
 * 
 * @author HungTT
 *
 */

@Stateless
public class JpaAnnualLeaveRemainHistRepository extends JpaRepository implements AnnualLeaveRemainHistRepository {
	@Override
	public void addOrUpdate(AnnualLeaveRemainingHistory domain) {
		KrcdtAnnLeaRemainHistPK krcdtAnnLeaRemainHistPK = new KrcdtAnnLeaRemainHistPK(domain.getEmployeeId(), domain.getYearMonth().v(),
				domain.getClosureId().value, domain.getClosureDate().getClosureDay().v(), domain.getClosureDate().getLastDayOfMonth(), domain.getGrantDate());
		Optional<KrcdtAnnLeaRemainHist> opt = this.queryProxy().find(krcdtAnnLeaRemainHistPK,
				KrcdtAnnLeaRemainHist.class);
		if (opt.isPresent()) {
			KrcdtAnnLeaRemainHist entity = opt.get();
//			entity.cid = domain.getCid();
			entity.deadline = domain.getDeadline();
			entity.expStatus = domain.getExpirationStatus().value;
			entity.registerType = domain.getRegisterType().value;
			entity.deductedDays = domain.getAnnualLeaveConditionInfo().isPresent()
					? domain.getAnnualLeaveConditionInfo().get().getDeductedDays().v() : null;
			entity.prescribedDays = domain.getAnnualLeaveConditionInfo().isPresent()
					? domain.getAnnualLeaveConditionInfo().get().getPrescribedDays().v() : null;
			entity.workingDays = domain.getAnnualLeaveConditionInfo().isPresent()
					? domain.getAnnualLeaveConditionInfo().get().getWorkingDays().v() : null;
			entity.remainingDays = domain.getDetails().getRemainingNumber().getDays().v();
			entity.remaningMinutes = domain.getDetails().getRemainingNumber().getMinutes().isPresent()
					? domain.getDetails().getRemainingNumber().getMinutes().get().v() : null;
			entity.grantDays = domain.getDetails().getGrantNumber().getDays().v();
			entity.grantMinutes = domain.getDetails().getGrantNumber().getMinutes().isPresent()
					? domain.getDetails().getGrantNumber().getMinutes().get().v() : null;
			entity.stowageDays = domain.getDetails().getUsedNumber().getStowageDays().isPresent()
					? domain.getDetails().getUsedNumber().getStowageDays().get().v() : null;
			entity.usedDays = domain.getDetails().getUsedNumber().getDays().v();
			entity.usedMinutes = domain.getDetails().getUsedNumber().getMinutes().isPresent()
					? domain.getDetails().getUsedNumber().getMinutes().get().v() : null;
			entity.usedPercent = domain.getDetails().getUsedPercent().v().doubleValue();
			this.commandProxy().update(entity);
		} else
			this.commandProxy().insert(KrcdtAnnLeaRemainHist.fromDomain(domain));
	}

	@Override
	public void delete(String employeeId, YearMonth ym, ClosureId closureId, ClosureDate closureDate) {
		String sql = "DELETE FROM KrcdtAnnLeaRemainHist a WHERE a.krcdtAnnLeaRemainHistPK.sid = :employeeId and a.krcdtAnnLeaRemainHistPK.yearMonth = :ym AND a.krcdtAnnLeaRemainHistPK.closureId = :closureId AND a.krcdtAnnLeaRemainHistPK.closeDay = :closeDay AND a.krcdtAnnLeaRemainHistPK.isLastDay = :isLastDay";
		this.getEntityManager().createQuery(sql).setParameter("employeeId", employeeId).setParameter("ym", ym.v())
				.setParameter("closureId", closureId.value).setParameter("closeDay", closureDate.getClosureDay().v())
				.setParameter("isLastDay", closureDate.getLastDayOfMonth() ? 1 : 0);
	}

	@Override
	public List<AnnualLeaveRemainingHistory> getInfoBySidAndYM(String sid, YearMonth ym) {
		String sql = "SELECT c FROM KrcdtAnnLeaRemainHist c "
				+ " WHERE c.krcdtAnnLeaRemainHistPK.sid = :sid"
				+ " AND c.krcdtAnnLeaRemainHistPK.yearMonth = :yearMonth";
		return this.queryProxy().query(sql, KrcdtAnnLeaRemainHist.class)
				.setParameter("sid", sid)
				.setParameter("yearMonth", ym)
				.getList(item -> item.toDomain());
	}

	@Override
	public List<AnnualLeaveRemainingHistory> getInfoByExpStatus(String sid, YearMonth ym, ClosureId closureID,
			ClosureDate closureDate, LeaveExpirationStatus expStatus, DatePeriod datePeriod) {
		String sql = "SELECT c FROM KrcdtAnnLeaRemainHist c "
				+ " WHERE c.krcdtAnnLeaRemainHistPK.sid = :sid"
				+ " AND c.krcdtAnnLeaRemainHistPK.yearMonth = :yearMonth"
				+ " AND c.krcdtAnnLeaRemainHistPK.closureId = :closureID"
				+ " AND c.krcdtAnnLeaRemainHistPK.closeDay = :closeDay"
				+ " AND c.krcdtAnnLeaRemainHistPK.isLastDay = :isLastDay"
				+ " AND c.expStatus = :expStatus"
				+ " AND c.deadline >= :startDate"
				+ " AND c.deadline <= :endDate"
				+ " ORDER BY c.krcdtAnnLeaRemainHistPK.grantDate ASC";
		return this.queryProxy().query(sql, KrcdtAnnLeaRemainHist.class)
				.setParameter("sid", sid)
				.setParameter("yearMonth", ym)
				.setParameter("closureID", closureID.value)
				.setParameter("closeDay", closureDate.getClosureDay().v())
				.setParameter("isLastDay", closureDate.getLastDayOfMonth() ? 1 : 0)
				.setParameter("expStatus", expStatus.value)
				.setParameter("startDate", datePeriod.start())
				.setParameter("endDate", datePeriod.end())
				.getList(item -> item.toDomain());
	}

}
