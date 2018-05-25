package nts.uk.ctx.at.record.infra.repository.workrecord.erroralarm.monthlycheckcondition;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.monthlycheckcondition.AgreementCheckCon36;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.monthlycheckcondition.AgreementCheckCon36Repository;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.monthlycheckcondition.KrcmtAgreementCheckCon36;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.monthlycheckcondition.KrcmtAgreementCheckCon36PK;
@Stateless
public class JpaAgreeCheckCon36Repository extends JpaRepository implements AgreementCheckCon36Repository {

	private final String SELECT_AGREE_BY_ID = " SELECT c FROM KrcmtAgreementCheckCon36 c"
			+ " WHERE c.krcmtAgreementCheckCon36PK.errorAlarmCheckID = :errorAlarmCheckID ";
	
	
	@Override
	public List<AgreementCheckCon36> getAgreementCheckCon36ById(String errorAlarmCheckID) {
		List<AgreementCheckCon36> data = this.queryProxy().query(SELECT_AGREE_BY_ID,KrcmtAgreementCheckCon36.class)
				.setParameter("errorAlarmCheckID", errorAlarmCheckID)
				.getList(c->c.toDomain());
		return data;
	}

	@Override
	public void addAgreementCheckCon36(AgreementCheckCon36 agreementCheckCon36) {
		this.commandProxy().insert(KrcmtAgreementCheckCon36.toEntity(agreementCheckCon36));
	}

	@Override
	public void updateAgreementCheckCon36(AgreementCheckCon36 agreementCheckCon36) {
		KrcmtAgreementCheckCon36 newEntity = KrcmtAgreementCheckCon36.toEntity(agreementCheckCon36);
		KrcmtAgreementCheckCon36 updateEntity = this.queryProxy().find(
				new KrcmtAgreementCheckCon36PK(agreementCheckCon36.getErrorAlarmCheckID(),agreementCheckCon36.getClassification().value), KrcmtAgreementCheckCon36.class).get();
		updateEntity.compareOperator = newEntity.compareOperator;
		updateEntity.eralBeforeTime = newEntity.eralBeforeTime;
		this.commandProxy().update(updateEntity);
	}

	@Override
	public void deleteAgreementCheckCon36(String errorAlarmCheckID) {
		List<KrcmtAgreementCheckCon36> newEntity = this.queryProxy().query(SELECT_AGREE_BY_ID,KrcmtAgreementCheckCon36.class)
				.setParameter("errorAlarmCheckID", errorAlarmCheckID)
				.getList();
		this.commandProxy().removeAll(newEntity);
		
	}

}
