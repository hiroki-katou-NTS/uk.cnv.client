package nts.uk.ctx.at.record.infra.repository.workrecord.erroralarm;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import org.apache.commons.lang3.BooleanUtils;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.FixedConditionWorkRecord;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.FixedConditionWorkRecordRepository;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.KrcmtFixedConditionWorkRecord;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.KrcmtFixedConditionWorkRecordPK;

@Stateless
public class JpaFixedConWorkRecordRepository extends JpaRepository implements  FixedConditionWorkRecordRepository {

	private static final String SELECT_FROM_FIXED_CON = " SELECT c FROM KrcmtFixedConditionWorkRecord c ";
	
	private static final String SELECT_FIXED_CON_BY_ALARM_ID =SELECT_FROM_FIXED_CON 
			+ " WHERE c.krcmtFixedConditionWorkRecordPK.dailyAlarmConID = :dailyAlarmConID ";
	
	private static final String SELECT_FIXED_CON_BY_CODE =SELECT_FIXED_CON_BY_ALARM_ID 
			+ " AND c.krcmtFixedConditionWorkRecordPK.fixConWorkRecordNo = :fixConWorkRecordNo ";
	
	private static final String DELETE_FIXED_CON_BY_DAILY_ID =  "DELETE FROM KrcmtFixedConditionWorkRecord c "
			+ " WHERE c.krcmtFixedConditionWorkRecordPK.dailyAlarmConID = :dailyAlarmConID ";
	
	private static final String SELECT_FIXED_CON_BY_ALARM_ID_USE = SELECT_FIXED_CON_BY_ALARM_ID 
			+ " AND c.useAtr = :useAtr ";
			
	
	@Override
	public List<FixedConditionWorkRecord> getAllFixedConditionWorkRecord() {
		List<FixedConditionWorkRecord> data = this.queryProxy().query(SELECT_FROM_FIXED_CON,KrcmtFixedConditionWorkRecord.class)
				.getList(c->c.toDomain());
		return data;
	}



	@Override
	public Optional<FixedConditionWorkRecord> getFixedConWRByCode(String dailyAlarmConID,int fixConWorkRecordNo) {
		Optional<FixedConditionWorkRecord> data = this.queryProxy().query(SELECT_FIXED_CON_BY_CODE,KrcmtFixedConditionWorkRecord.class)
				.setParameter("dailyAlarmConID", dailyAlarmConID)
				.setParameter("fixConWorkRecordNo", fixConWorkRecordNo)
				.getSingle(c->c.toDomain());
		return data;
	}

	@Override
	public void addFixedConWorkRecord(FixedConditionWorkRecord fixedConditionWorkRecord) {
		this.commandProxy().insert(KrcmtFixedConditionWorkRecord.toEntity(fixedConditionWorkRecord));
		this.getEntityManager().flush();
		
	}

	@Override
	public void updateFixedConWorkRecord(FixedConditionWorkRecord fixedConditionWorkRecord) {
		KrcmtFixedConditionWorkRecord newEntity = KrcmtFixedConditionWorkRecord.toEntity(fixedConditionWorkRecord);
		KrcmtFixedConditionWorkRecord updateEntity = this.queryProxy().find(
				new KrcmtFixedConditionWorkRecordPK(
						newEntity.krcmtFixedConditionWorkRecordPK.dailyAlarmConID,
						newEntity.krcmtFixedConditionWorkRecordPK.fixConWorkRecordNo
						),
				KrcmtFixedConditionWorkRecord.class).get();
		updateEntity.message = newEntity.message;
		updateEntity.useAtr = newEntity.useAtr;
		this.commandProxy().update(updateEntity);
	}

	
	@Override
	public void deleteFixedConWorkRecord(String dailyAlarmConID) {
		this.getEntityManager().createQuery(DELETE_FIXED_CON_BY_DAILY_ID)
		.setParameter("dailyAlarmConID", dailyAlarmConID).executeUpdate();
	}



	@Override
	public List<FixedConditionWorkRecord> getAllFixedConWorkRecordByID(String dailyAlarmConID) {
		List<FixedConditionWorkRecord> data = this.queryProxy().query(SELECT_FIXED_CON_BY_ALARM_ID,KrcmtFixedConditionWorkRecord.class)
				.setParameter("dailyAlarmConID", dailyAlarmConID)
				.getList(c->c.toDomain());
		return data;
	}
	
	@Override
	public List<FixedConditionWorkRecord> getAllFixedConWorkRecordByID(List<String> dailyAlarmConID) {
		String SELECT_FIXED_CON_BY_ALARM_ID =SELECT_FROM_FIXED_CON 
				+ " WHERE c.krcmtFixedConditionWorkRecordPK.dailyAlarmConID in :dailyAlarmConID ";
		List<FixedConditionWorkRecord> data = this.queryProxy().query(SELECT_FIXED_CON_BY_ALARM_ID,KrcmtFixedConditionWorkRecord.class)
				.setParameter("dailyAlarmConID", dailyAlarmConID)
				.getList(c->c.toDomain());
		return data;
	}

	@Override
	public List<FixedConditionWorkRecord> getFixConWorkRecordByIdUse (String dailyAlarmConID, int use) {
		List<FixedConditionWorkRecord> data = this.queryProxy().query(SELECT_FIXED_CON_BY_ALARM_ID_USE, KrcmtFixedConditionWorkRecord.class)
				.setParameter("dailyAlarmConID", dailyAlarmConID)
				.setParameter("useAtr", BooleanUtils.toBoolean(use))
				.getList(c->c.toDomain());
		return data;
	}

}
