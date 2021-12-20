package nts.uk.ctx.sys.assist.infra.repository.deletedata;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import org.apache.commons.lang3.BooleanUtils;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.sys.assist.dom.deletedata.ManagementDeletion;
import nts.uk.ctx.sys.assist.dom.deletedata.ManagementDeletionRepository;
import nts.uk.ctx.sys.assist.dom.deletedata.OperatingCondition;
import nts.uk.ctx.sys.assist.infra.entity.deletedata.SspttDeletionMng;
import nts.uk.ctx.sys.assist.infra.entity.deletedata.SspdtManagementDeletionPK;

@Stateless
public class JpaManagementDeletionRepository extends JpaRepository implements ManagementDeletionRepository {

	private static final String SELECT_ALL_QUERY_STRING = "SELECT f FROM SspttDeletionMng f";
	private static final String SELECT_BY_KEY_STRING = SELECT_ALL_QUERY_STRING
			+ " WHERE  f.sspdtManagementDeletionPK.delId =:delId ";

	@Override
	public List<ManagementDeletion> getAllManagementDeletion() {
		return this.queryProxy().query(SELECT_ALL_QUERY_STRING, SspttDeletionMng.class)
				.getList(item -> item.toDomain());
	}

	@Override
	public Optional<ManagementDeletion> getManagementDeletionById(String delId) {
		return this.queryProxy().query(SELECT_BY_KEY_STRING, SspttDeletionMng.class)
				.setParameter("delId", delId).getSingle(c -> c.toDomain());
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	@Override
	public void add(ManagementDeletion domain) {
		this.commandProxy().insert(SspttDeletionMng.toEntity(domain));
		this.getEntityManager().flush();
	}

	@Override
	public void updateTotalCatCount(String delId, int totalCategoryCount) {
		SspdtManagementDeletionPK sspdtManagementDeletionPK = new  SspdtManagementDeletionPK(delId);
		Optional<SspttDeletionMng> entityOpt = this.queryProxy().find(sspdtManagementDeletionPK, SspttDeletionMng.class);
		if (entityOpt.isPresent()) {
			SspttDeletionMng entity = entityOpt.get();
			entity.totalCategoryCount = totalCategoryCount;
			this.commandProxy().update(entity);
		}
	}

	@Override
	public void updateCatCountAnCond(String delId, int categoryCount, OperatingCondition operatingCondition) {
		SspdtManagementDeletionPK sspdtManagementDeletionPK = new  SspdtManagementDeletionPK(delId);
		Optional<SspttDeletionMng> entityOpt = this.queryProxy().find(sspdtManagementDeletionPK, SspttDeletionMng.class);
		if (entityOpt.isPresent()) {
			SspttDeletionMng entity = entityOpt.get();
			entity.categoryCount = categoryCount;
			entity.operatingCondition = operatingCondition.value;
			this.commandProxy().update(entity);
		}
	}

	@Override
	public void update(ManagementDeletion managementDeletion) {
		SspdtManagementDeletionPK sspdtManagementDeletionPK = new  SspdtManagementDeletionPK(managementDeletion.getDelId());
		Optional<SspttDeletionMng> entityOpt = this.queryProxy().find(sspdtManagementDeletionPK, SspttDeletionMng.class);
		if (entityOpt.isPresent()) {
			SspttDeletionMng entity = entityOpt.get();
			entity.errorCount = managementDeletion.errorCount;
			this.commandProxy().update(entity);
		}
	}
	
	@Override
	public void updateCatCount(String delId, int categoryCount) {
		SspdtManagementDeletionPK sspdtManagementDeletionPK = new  SspdtManagementDeletionPK(delId);
		Optional<SspttDeletionMng> entityOpt = this.queryProxy().find(sspdtManagementDeletionPK, SspttDeletionMng.class);
		if (entityOpt.isPresent()) {
			SspttDeletionMng entity = entityOpt.get();
			entity.categoryCount = categoryCount;
			this.commandProxy().update(entity);
		}
	}

	@Override
	public void updateOperationCond(String delId, OperatingCondition operatingCondition) {
		SspdtManagementDeletionPK sspdtManagementDeletionPK = new  SspdtManagementDeletionPK(delId);
		Optional<SspttDeletionMng> entityOpt = this.queryProxy().find(sspdtManagementDeletionPK, SspttDeletionMng.class);
		if (entityOpt.isPresent()) {
			SspttDeletionMng entity = entityOpt.get();
			entity.operatingCondition = operatingCondition.value;
			this.commandProxy().update(entity);
		}
	}
	
	@Override
	public void setInterruptDeleting(String delId,int interruptedFlg,OperatingCondition operatingCondition ) {
		SspdtManagementDeletionPK sspdtManagementDeletionPK = new  SspdtManagementDeletionPK(delId);
		Optional<SspttDeletionMng> optEntity = this.queryProxy().find(sspdtManagementDeletionPK, SspttDeletionMng.class);
		if (optEntity.isPresent()) {
			SspttDeletionMng entity = optEntity.get();
			entity.operatingCondition = operatingCondition.value;
			entity.isInterruptedFlg = BooleanUtils.toBoolean(interruptedFlg);
			this.commandProxy().update(entity);
		}
	}
	
	@Override
    public void remove(String delId) {
		SspdtManagementDeletionPK sspdtManagementDeletionPK = new  SspdtManagementDeletionPK(delId);
		Optional<SspttDeletionMng> optEntity = this.queryProxy().find(sspdtManagementDeletionPK, SspttDeletionMng.class);
		if (optEntity.isPresent()) {
			this.commandProxy().remove(SspttDeletionMng.class, sspdtManagementDeletionPK);
		}
    }
}
