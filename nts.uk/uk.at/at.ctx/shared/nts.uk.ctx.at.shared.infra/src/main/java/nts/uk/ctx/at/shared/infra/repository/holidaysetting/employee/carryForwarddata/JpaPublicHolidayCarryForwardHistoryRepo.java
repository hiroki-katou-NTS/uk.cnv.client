package nts.uk.ctx.at.shared.infra.repository.holidaysetting.employee.carryForwarddata;

import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardHistory;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardHistoryRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.ctx.at.shared.infra.entity.holidaysetting.employee.carryForwarddata.KshdtHdpubRemHist;
import nts.uk.ctx.at.shared.infra.entity.holidaysetting.employee.carryForwarddata.KshdtHdpubRemHistPK;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

@Stateless
public class JpaPublicHolidayCarryForwardHistoryRepo extends JpaRepository implements PublicHolidayCarryForwardHistoryRepository {

	
	private static final String REMOVE_BY_SID_YM_CLOSUREId_CLOSUREDATE = "DELETE FROM KshdtHdpubRemHist a"
			+" WHERE a.pk.sid = :employeeId"
			+" AND a.pk.yearMonth >= :yearMonth "
			+" AND a.pk.closureId = :closureId"
			+" AND a.pk.closeDay = :closeDay"
			+" AND a.pk.isLastDay = :isLastDay";
	
	/*
	 * (非 Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardHistoryRepository#persistAndUpdate(nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardHistory)
	 */
	public void persistAndUpdate(PublicHolidayCarryForwardHistory domain){
		KshdtHdpubRemHistPK pk = new KshdtHdpubRemHistPK(
				domain.getEmployeeId(),
				domain.getHistYearMonth().v(),
				domain.getClosureId().value,
				domain.getClosureDate().getClosureDay().v(),
				domain.getClosureDate().getLastDayOfMonth()
				);
		
		Optional<KshdtHdpubRemHist> entityOpt = this.queryProxy().find(pk, KshdtHdpubRemHist.class);
		
		if (entityOpt.isPresent()) {
			entityOpt.get().fromDomainForUpdate(domain);
			this.commandProxy().update(entityOpt.get());
			this.getEntityManager().flush();
			return;
		}
		
		KshdtHdpubRemHist entity = new KshdtHdpubRemHist();
		entity.fromDomainForInsert(domain);
		this.commandProxy().insert(entity);
		this.getEntityManager().flush();
	}
	
	/*
	 * (非 Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardHistoryRepository#deleteThisMonthAfter(java.lang.String, nts.arc.time.YearMonth, nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId, nts.uk.shr.com.time.calendar.date.ClosureDate)
	 */
	public void deleteThisMonthAfter(
			String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate){
		this.queryProxy().query(REMOVE_BY_SID_YM_CLOSUREId_CLOSUREDATE,KshdtHdpubRemHist.class)
		.setParameter("employeeId", employeeId)
		.setParameter("yearMonth", yearMonth)
		.setParameter("closureId", closureId.value)
		.setParameter("closeDay", closureDate.getClosureDay().v())
		.setParameter("isLastDay", closureDate.getLastDayOfMonth() ? 1 : 0);
	}
}
