package nts.uk.ctx.at.record.infra.repository.workrecord.erroralarm.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import org.apache.commons.lang3.BooleanUtils;

import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.WorkRecordExtraConRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.WorkRecordExtractingCondition;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.KrcmtWorkRecordExtraCon;

@Stateless
public class JpaWorkRecorExtraConRepository extends JpaRepository implements WorkRecordExtraConRepository  {

	
	private static final String SELECT_FROM_WORK_RECORD = " SELECT c FROM KrcmtWorkRecordExtraCon c ";
	private static final String SELECT_FROM_WORK_RECORD_BY_ID = SELECT_FROM_WORK_RECORD 
			+ " WHERE c.errorAlarmCheckID = :errorAlarmCheckID ";
	private static final String SELECT_WREC_BY_LIST_ID = "SELECT c FROM KrcmtWorkRecordExtraCon c WHERE c.errorAlarmCheckID IN :listErrorAlarmID";
	
	private static final String SELECT_FROM_WORK_RECORD_BY_ID_USE = SELECT_WREC_BY_LIST_ID + " AND c.useAtr = :useAtr ";
	
	@Override
	public List<WorkRecordExtractingCondition> getAllWorkRecordExtraCon() {
		List<WorkRecordExtractingCondition> data = this.queryProxy().query(SELECT_FROM_WORK_RECORD,KrcmtWorkRecordExtraCon.class)
				.getList(c->c.toDomain()); 
		return data;
	}

	@Override
	public Optional<WorkRecordExtractingCondition> getWorkRecordExtraConById(String errorAlarmCheckID) {
		Optional<WorkRecordExtractingCondition> data = this.queryProxy().query(SELECT_FROM_WORK_RECORD_BY_ID,KrcmtWorkRecordExtraCon.class)
				.setParameter("errorAlarmCheckID", errorAlarmCheckID)
				.getSingle(c->c.toDomain());
				
		return data;
	}

	@Override
	public void addWorkRecordExtraCon(WorkRecordExtractingCondition workRecordExtractingCondition) {
		this.commandProxy().insert(KrcmtWorkRecordExtraCon.toEntity(workRecordExtractingCondition));
		this.getEntityManager().flush();
	}

	@Override
	public void updateWorkRecordExtraCon(WorkRecordExtractingCondition workRecordExtractingCondition) {
		KrcmtWorkRecordExtraCon newEntity = KrcmtWorkRecordExtraCon.toEntity(workRecordExtractingCondition);
		KrcmtWorkRecordExtraCon updateEntity = this.queryProxy().find(newEntity.errorAlarmCheckID, KrcmtWorkRecordExtraCon.class).get();
		updateEntity.messageBold = newEntity.messageBold;
		updateEntity.messageColor = newEntity.messageColor;
		updateEntity.sortOrderBy = newEntity.sortOrderBy;
		updateEntity.checkItem = newEntity.checkItem;
		updateEntity.useAtr = newEntity.useAtr;
		updateEntity.nameWKRecord = newEntity.nameWKRecord;
		this.commandProxy().update(updateEntity);
	}

	@Override
	public void deleteWorkRecordExtraCon(String errorAlarmCheckID) {
		this.commandProxy().remove(KrcmtWorkRecordExtraCon.class,errorAlarmCheckID);
		
	}
	
	@Override
	public List<WorkRecordExtractingCondition> getAllWorkRecordExtraConByListID(List<String> listErrorAlarmID) {
		List<WorkRecordExtractingCondition> datas = new ArrayList<>();
		CollectionUtil.split(listErrorAlarmID, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
			datas.addAll(this.queryProxy().query(SELECT_WREC_BY_LIST_ID, KrcmtWorkRecordExtraCon.class)
					.setParameter("listErrorAlarmID", subIdList).getList(c->c.toDomain()));
		});
		return datas;
	}

	@Override
	public List<WorkRecordExtractingCondition> getAllWorkRecordExtraConByIdAndUse(List<String> listErrorAlarmID,
			int use) {
		
		List<WorkRecordExtractingCondition> workRecord = new ArrayList<>();
		CollectionUtil.split(listErrorAlarmID, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList ->{
			workRecord.addAll(this.queryProxy().query(SELECT_FROM_WORK_RECORD_BY_ID_USE, KrcmtWorkRecordExtraCon.class)
					.setParameter("listErrorAlarmID", listErrorAlarmID)
					.setParameter("useAtr", BooleanUtils.toBoolean(use))
					.getList(c->c.toDomain()));
		});
		
		return workRecord;
	}

}
