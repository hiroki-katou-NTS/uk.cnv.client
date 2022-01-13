package nts.uk.ctx.at.shared.infra.repository.scherec.monthlyattdcal.aggr.vtotalmethod;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.vtotalmethod.LeaveCountedAsWorkDaysType;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.vtotalmethod.WorkDaysNumberOnLeaveCount;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.vtotalmethod.WorkDaysNumberOnLeaveCountRepository;
import nts.uk.ctx.at.shared.infra.entity.scherec.monthlyattdcal.aggr.vtotalmethod.KrcmtCalcMAttdCountVacation;
import nts.uk.ctx.at.shared.infra.entity.scherec.monthlyattdcal.aggr.vtotalmethod.KrcmtCalcMAttdCountVacationPK;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class JpaWorkDaysNumberOnLeaveCountRepository extends JpaRepository
		implements WorkDaysNumberOnLeaveCountRepository {

	private static final String SELECT_BY_CID = "SELECT t FROM KrcmtCalcMAttdCountVacation t "
			+ "WHERE t.pk.cid = :cid";

	private WorkDaysNumberOnLeaveCount toDomain(String cid, List<KrcmtCalcMAttdCountVacation> entities) {
		List<LeaveCountedAsWorkDaysType> leaveTypes = entities.stream()
				.map(entity -> EnumAdaptor.valueOf(entity.pk.vacationType, LeaveCountedAsWorkDaysType.class))
				.collect(Collectors.toList());
		return new WorkDaysNumberOnLeaveCount(cid, leaveTypes);
	}

	public KrcmtCalcMAttdCountVacation toEntity(String cid, int leaveType) {
		KrcmtCalcMAttdCountVacationPK pk = new KrcmtCalcMAttdCountVacationPK(cid, leaveType);
		return new KrcmtCalcMAttdCountVacation(pk, AppContexts.user().contractCode());
	}

	@Override
	public void insert(String cid, int leaveType) {
		this.commandProxy().insert(this.toEntity(cid, leaveType));
	}

	@Override
	public void delete(String cid, int leaveType) {
		this.commandProxy().remove(KrcmtCalcMAttdCountVacation.class,
				new KrcmtCalcMAttdCountVacationPK(cid, leaveType));
	}

	@Override
	public WorkDaysNumberOnLeaveCount findByCid(String cid) {
		List<KrcmtCalcMAttdCountVacation> entities = this.queryProxy()
				.query(SELECT_BY_CID, KrcmtCalcMAttdCountVacation.class).setParameter("cid", cid).getList();
		return this.toDomain(cid, entities);
	}

}
