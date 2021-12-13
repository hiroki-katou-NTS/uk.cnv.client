package nts.uk.ctx.at.record.infra.repository.workrecord.erroralarm.mastercheck;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import lombok.experimental.var;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.mastercheck.ErrorAlarmMessageMSTCHK;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.mastercheck.MasterCheckFixedCheckItem;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.mastercheck.MasterCheckFixedExtractCondition;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.mastercheck.MasterCheckFixedExtractConditionRepository;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.mastercheck.KrcmtMasterCheckFixedExtractCondition;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.mastercheck.KrcmtMasterCheckFixedExtractConditionPK;

@Stateless
public class JpaMasterCheckFixedExtractConditionRepository extends JpaRepository
	implements MasterCheckFixedExtractConditionRepository {
	
	private static final String DELETE_MASTER_CHECK_FIXED_CON_BY_ID =  "DELETE FROM KrcmtMasterCheckFixedExtractCondition c "
			+ " WHERE c.pk.erAlId = :erAlId ";
	
	private static final String SELECT_MASTER_CHECK_FIXED_CON_BY_ID =  "SELECT c FROM KrcmtMasterCheckFixedExtractCondition c "
			+ " WHERE c.pk.erAlId = :erAlId ";

	@Override
	public List<MasterCheckFixedExtractCondition> findAll(String extractConditionIds, boolean useAtr) {
		String query = "SELECT a FROM KrcmtMasterCheckFixedExtractCondition a WHERE a.useAtr = :useAtr AND a.pk.erAlId = :ids";
		List<KrcmtMasterCheckFixedExtractCondition> results = new ArrayList<>();
		
		results.addAll(this.queryProxy().query(query, KrcmtMasterCheckFixedExtractCondition.class)
				.setParameter("useAtr", useAtr ? 1 : 0).setParameter("ids", extractConditionIds).getList());
		
		return results.stream().map(a -> new MasterCheckFixedExtractCondition(
					a.getPk().getErAlId(), 
					EnumAdaptor.valueOf(a.getPk().getNo(), MasterCheckFixedCheckItem.class), 
					Optional.ofNullable(new ErrorAlarmMessageMSTCHK(a.getMessage())), 
					a.isUseAtr()))
				.collect(Collectors.toList());
	}

	@Override
	public List<MasterCheckFixedExtractCondition> getAllFixedMasterCheckConById(String errorAlarmCheckId) {
		List<MasterCheckFixedExtractCondition> data = this.queryProxy().query(SELECT_MASTER_CHECK_FIXED_CON_BY_ID, KrcmtMasterCheckFixedExtractCondition.class)
				.setParameter("erAlId", errorAlarmCheckId)
				.getList(c->c.toDomain());
		return data;
	}

	@Override
	public void addMasterCheckFixedCondition(MasterCheckFixedExtractCondition masterCheckFixedCondition) {
		this.commandProxy().insert(KrcmtMasterCheckFixedExtractCondition.toEntity(masterCheckFixedCondition));
		this.getEntityManager().flush();
	}

	@Override
	public void updateMasterCheckFixedCondition(MasterCheckFixedExtractCondition masterCheckFixedCondition) {
		KrcmtMasterCheckFixedExtractCondition newEntity = KrcmtMasterCheckFixedExtractCondition.toEntity(masterCheckFixedCondition);
		KrcmtMasterCheckFixedExtractCondition updateEntity = this.queryProxy().find(
				new KrcmtMasterCheckFixedExtractConditionPK(
						newEntity.getPk().getErAlId(), 
						newEntity.getPk().getNo()),
				KrcmtMasterCheckFixedExtractCondition.class).get();
		updateEntity.setMessage(newEntity.getMessage());
		updateEntity.setUseAtr(newEntity.isUseAtr());
		this.commandProxy().update(updateEntity);
	}

	@Override
	public void deleteMasterCheckFixedCondition(String errorAlarmCheckId) {
		this.getEntityManager().createQuery(DELETE_MASTER_CHECK_FIXED_CON_BY_ID)
		.setParameter("erAlId", errorAlarmCheckId).executeUpdate();
	}

	@Override
	public void persist(List<MasterCheckFixedExtractCondition> condition) {
		condition.stream().forEach(x -> {
			KrcmtMasterCheckFixedExtractConditionPK pk = new KrcmtMasterCheckFixedExtractConditionPK(x.getErrorAlarmCheckId(), x.getNo().value);
			KrcmtMasterCheckFixedExtractCondition entity = this.getEntityManager().find(KrcmtMasterCheckFixedExtractCondition.class, pk);
			if(entity == null) {
				this.getEntityManager().persist(KrcmtMasterCheckFixedExtractCondition.toEntity(x));
			} else {
				entity.setMessage(x.getMessage().isPresent() ? x.getMessage().get().v() : "");
				entity.setUseAtr(x.isUseAtr());
				this.commandProxy().update(entity);	
			}
		});		
	}
}
