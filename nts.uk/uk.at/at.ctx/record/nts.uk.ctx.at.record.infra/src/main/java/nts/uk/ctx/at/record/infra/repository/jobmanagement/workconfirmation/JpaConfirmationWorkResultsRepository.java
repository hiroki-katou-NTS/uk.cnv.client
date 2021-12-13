package nts.uk.ctx.at.record.infra.repository.jobmanagement.workconfirmation;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.jobmanagement.workconfirmation.ConfirmationWorkResults;
import nts.uk.ctx.at.record.dom.jobmanagement.workconfirmation.ConfirmationWorkResultsRepository;
import nts.uk.ctx.at.record.infra.entity.jobmanagement.workconfirmation.KrcdtTaskConfirm;
import nts.uk.ctx.at.record.infra.entity.jobmanagement.workconfirmation.KrcdtTaskConfirmPK;

@Stateless
public class JpaConfirmationWorkResultsRepository extends JpaRepository implements ConfirmationWorkResultsRepository{

	private static String SELECT = "Select e From KrcdtTaskConfirm ";
	
	@Override
	public void insert(ConfirmationWorkResults confirmationWorkResults) {
		this.commandProxy().insert(new KrcdtTaskConfirm(confirmationWorkResults));
		this.getEntityManager().flush();
	}

	@Override
	public void update(ConfirmationWorkResults confirmationWorkResults) {
		this.commandProxy().update(new KrcdtTaskConfirm(confirmationWorkResults));
		this.getEntityManager().flush();
	}

	@Override
	public void delete(ConfirmationWorkResults confirmationWorkResults) {
		this.commandProxy().remove(KrcdtTaskConfirm.class, new KrcdtTaskConfirmPK(confirmationWorkResults.getTargetSID(), confirmationWorkResults.getTargetYMD()));
		this.getEntityManager().flush();
	}

	@Override
	public Optional<ConfirmationWorkResults> get(String targetSID, GeneralDate targetYMD) {
		return this.queryProxy().find(new KrcdtTaskConfirmPK(targetSID, targetYMD), KrcdtTaskConfirm.class).map(c->c.toDomain());
	}

	@Override
	public List<ConfirmationWorkResults> get(String targetSid, GeneralDate startDate, GeneralDate endDate) {
		return this.queryProxy().query(SELECT + "where e.pk.targetSid = :targetSid AND e.pk.targetYMD BETWEEN :startDate AND :endDate", 
				KrcdtTaskConfirm.class)
				.setParameter("targetSid", targetSid)
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate)
				.getList(c->c.toDomain());
	}

	@Override
	public Optional<ConfirmationWorkResults> get(String targetSid, String confirmerSid, GeneralDate targetYMD) {
		return this.queryProxy().query(SELECT + "where e.pk.targetSid = :targetSid AND e.pk.targetYMD = :targetYMD "
				+ "AND (e.confirmSid1 = :confirmerSid OR e.confirmSid2 = :confirmerSid OR e.confirmSid3 = :confirmerSid OR e.confirmSid4 = :confirmerSid OR e.confirmSid5 = :confirmerSid)", 
				KrcdtTaskConfirm.class)
				.setParameter("targetSid", targetSid)
				.setParameter("confirmerSid", confirmerSid)
				.setParameter("targetYMD", targetYMD)
				.getSingle(c->c.toDomain());
	}

	@Override
	public List<ConfirmationWorkResults> get(String targetSid, String confirmerSid, GeneralDate startDate,GeneralDate endDate) {
		return this.queryProxy().query(SELECT + "where e.pk.targetSid = :targetSid AND e.pk.targetYMD BETWEEN :startDate AND :endDate "
				+ "AND (e.confirmSid1 = :confirmerSid OR e.confirmSid2 = :confirmerSid OR e.confirmSid3 = :confirmerSid OR e.confirmSid4 = :confirmerSid OR e.confirmSid5 = :confirmerSid)", 
				KrcdtTaskConfirm.class)
				.setParameter("targetSid", targetSid)
				.setParameter("confirmerSid", confirmerSid)
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate)
				.getList(c->c.toDomain());
	}

}
