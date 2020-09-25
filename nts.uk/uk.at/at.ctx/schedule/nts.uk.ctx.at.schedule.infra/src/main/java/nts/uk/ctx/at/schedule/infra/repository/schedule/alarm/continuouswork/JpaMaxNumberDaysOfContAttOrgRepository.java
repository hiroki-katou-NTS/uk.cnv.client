package nts.uk.ctx.at.schedule.infra.repository.schedule.alarm.continuouswork;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.uk.ctx.at.schedule.dom.schedule.alarm.continuouswork.continuousattendance.MaxNumberDaysOfContAttOrgRepository;
import nts.uk.ctx.at.schedule.dom.schedule.alarm.continuouswork.continuousattendance.MaxNumberDaysOfContinuousAttendanceOrg;
import nts.uk.ctx.at.schedule.infra.entity.schedule.alarm.continuouswork.KscmtAlchkConsecutiveWorkOrg;
import nts.uk.ctx.at.schedule.infra.entity.schedule.alarm.continuouswork.KscmtAlchkConsecutiveWorkOrgPk;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;

/**
 * 
 * @author hiroko_miura
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class JpaMaxNumberDaysOfContAttOrgRepository extends JpaRepository implements MaxNumberDaysOfContAttOrgRepository {

	@Override
	public void insert(MaxNumberDaysOfContinuousAttendanceOrg maxContAttOrg, String companyId) {
		this.commandProxy().insert(KscmtAlchkConsecutiveWorkOrg.of(maxContAttOrg, companyId));
	}

	@Override
	public void update(MaxNumberDaysOfContinuousAttendanceOrg maxContAttOrg, String companyId) {
		KscmtAlchkConsecutiveWorkOrg entity = KscmtAlchkConsecutiveWorkOrg.of(maxContAttOrg, companyId);
		
		KscmtAlchkConsecutiveWorkOrg updata = this.queryProxy()
				.find(entity.pk, KscmtAlchkConsecutiveWorkOrg.class)
				.get();
		
		updata.setMaxConsDays(maxContAttOrg.getNumberOfDays().getNumberOfDays().v());
		
		this.commandProxy().update(updata);
	}

	@Override
	public void delete(TargetOrgIdenInfor targeOrg, String companyId) {
		KscmtAlchkConsecutiveWorkOrgPk pk = new KscmtAlchkConsecutiveWorkOrgPk(
				  companyId
				, targeOrg.getUnit().value
				, targeOrg.getTargetId());
		
		this.commandProxy().remove(KscmtAlchkConsecutiveWorkOrg.class, pk);
	}

	@Override
	public boolean exists(TargetOrgIdenInfor targeOrg, String companyId) {
		return this.get(targeOrg, companyId).isPresent();
	}

	@Override
	public Optional<MaxNumberDaysOfContinuousAttendanceOrg> get(TargetOrgIdenInfor targeOrg, String companyId) {
		String sql = "SELECT * FROM KSCMT_ALCHK_CONSECUTIVE_WORK_ORG"
				+ " WHERE CID = @companyId"
				+ " AND TARGET_UNIT = @targetUnit"
				+ " AND TARGET_ID = @targetId";
		
		return new NtsStatement(sql, this.jdbcProxy())
				.paramString("companyId", companyId)
				.paramInt("TARGET_UNIT", targeOrg.getUnit().value)
				.paramString("TARGET_ID", targeOrg.getTargetId())
				.getSingle(x -> KscmtAlchkConsecutiveWorkOrg.MAPPER.toEntity(x).toDomain());
	}

}
