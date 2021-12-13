package nts.uk.ctx.at.record.infra.repository.workrecord.erroralarm.otkcustomize;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import lombok.val;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.database.DatabaseProduct;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.otkcustomize.ContinuousHolCheckSet;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.otkcustomize.repo.ContinuousHolCheckSetRepo;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue.ContinuousVacationDays;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue.DisplayMessage;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.otkcustomize.KrcmtOtkHdCk;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.otkcustomize.KrcctOtkWtNonTarget;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.otkcustomize.KrcmtOtkHdCkWktpTgt;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;

@Stateless
public class ContinuousHolCheckSetRepoImpl extends JpaRepository implements ContinuousHolCheckSetRepo {

	@Override
	public List<ContinuousHolCheckSet> find(List<String> companyIds){
		if(companyIds.isEmpty()){
			return new ArrayList<>();
		}
		List<ContinuousHolCheckSet> resultList = new ArrayList<>();
		CollectionUtil.split(companyIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query("SELECT v FROM KrcmtOtkHdCk v WHERE v.cid IN :cid", KrcmtOtkHdCk.class)
				.setParameter("cid", subList)
				.getList(c -> c.toDomain()));
		});
		return resultList;
	}

	@Override
	public Optional<ContinuousHolCheckSet> find(String companyId){
		return find(Arrays.asList(companyId)).stream()
					.filter(c -> c.getCompanyId().equals(companyId)).findFirst();
	}
	
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public Optional<ContinuousHolCheckSet> findSpecial(String companyId) {
		/* TODO: find a common way for join WORKTYPE_CD in tables in oracle and sql server */
//			StringBuilder queryString = new StringBuilder("SELECT a.CID, a.CONTINUOUS_DAYS, a.MESSAGE_DISPLAY, ");
//			queryString.append(" STUFF((SELECT '; ' + b.WORKTYPE_CD FROM KRCMT_OTK_HD_CK_WKTP_TGT b ");
//			queryString.append(" WHERE a.CID = b.CID FOR XML PATH('')), 1, 1, '') [TARGET], ");
//			queryString.append(" STUFF((SELECT '; ' + c.WORKTYPE_CD FROM KRCMT_OTK_HD_CK_WKTP_NTGT c ");
//			queryString.append(" WHERE a.CID = c.CID FOR XML PATH('')), 1, 1, '') [NONTARGET] ");
		StringBuilder queryString = new StringBuilder("SELECT a.CID, a.CONTINUOUS_DAYS, a.MESSAGE_DISPLAY,  ");
		queryString.append(" b.WORKTYPE_CD as TARGET, c.WORKTYPE_CD as NONTARGET ");
		queryString.append(" FROM KRCMT_OTK_HD_CK a ");
		queryString.append(" LEFT JOIN KRCMT_OTK_HD_CK_WKTP_TGT b ON a.CID = b.CID ");
		queryString.append(" LEFT JOIN KRCMT_OTK_HD_CK_WKTP_NTGT c ON a.CID = c.CID ");
		queryString.append(" WHERE a.CID = ? AND a.USE_ATR = ?");
		
		boolean isPostGres = this.database().is(DatabaseProduct.POSTGRESQL);
			
		try (PreparedStatement statement = this.connection().prepareStatement(queryString.toString())) {

			statement.setString(1, companyId);
			if (isPostGres) {
				statement.setBoolean(2, true);	
			} else {
				statement.setInt(2, 1);				
			}
			val result = new NtsResultSet(statement.executeQuery()).getList(rec -> {
				Map<String, Object> val = new HashMap<>();
				val.put("NONTARGET", rec.getString("NONTARGET"));
				val.put("TARGET", rec.getString("TARGET"));
				val.put("MESSAGE_DISPLAY", rec.getString("MESSAGE_DISPLAY"));
				val.put("CONTINUOUS_DAYS", rec.getInt("CONTINUOUS_DAYS"));
				val.put("CID", rec.getString("CID"));
				return val;
			});
			
			if(result.isEmpty()){
				return Optional.empty();
			}
			 return Optional.of(new ContinuousHolCheckSet(companyId, 
					 getType(result, "TARGET"), 
					 getType(result, "NONTARGET"), 
					 true, new DisplayMessage(result.get(0).get("MESSAGE_DISPLAY").toString()), 
					 new ContinuousVacationDays((int) result.get(0).get("CONTINUOUS_DAYS")))); 
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}



	private List<WorkTypeCode> getType(final List<Map<String, Object>> result, String column) {
		return result.stream().filter(c -> c.get(column) != null).map(c -> c.get(column).toString().trim())
				.distinct().map(c -> new WorkTypeCode(c)).collect(Collectors.toList());
	}

	@Override
	public void insert(ContinuousHolCheckSet setting){
		this.commandProxy().insert(KrcmtOtkHdCk.fromDomain(setting));
		this.commandProxy().insertAll(toNonTarget(setting.getIgnoreWorkType(), setting.getCompanyId()));
		this.commandProxy().insertAll(toTarget(setting.getTargetWorkType(), setting.getCompanyId()));
	}

	@Override
	public void update(ContinuousHolCheckSet setting){
		KrcmtOtkHdCk entity = findEntity(setting.getCompanyId());
		if(entity != null){
			if(!entity.krcctOtkWtNonTarget.isEmpty()){
				this.commandProxy().removeAll(entity.krcctOtkWtNonTarget);
			}
			if(!entity.krcctOtkWtTarget.isEmpty()){
				this.commandProxy().removeAll(entity.krcctOtkWtTarget);
			}
			this.getEntityManager().flush();
			entity.continuousDays = setting.getMaxContinuousDays().v();
			entity.messageDisplay = setting.getDisplayMessege().v();
			entity.useAtr = setting.isUseAtr();
			this.commandProxy().insertAll(toNonTarget(setting.getIgnoreWorkType(), setting.getCompanyId()));
			this.commandProxy().insertAll(toTarget(setting.getTargetWorkType(), setting.getCompanyId()));
			this.getEntityManager().flush();
		}
	}

	@Override
	public void remove(String cid){
		KrcmtOtkHdCk entity = findEntity(cid);
		if(entity != null){
			if(!entity.krcctOtkWtNonTarget.isEmpty()){
				this.commandProxy().removeAll(entity.krcctOtkWtNonTarget);
			}
			if(!entity.krcctOtkWtTarget.isEmpty()){
				this.commandProxy().removeAll(entity.krcctOtkWtTarget);
			}
			this.commandProxy().remove(entity);
		}
	}

	private KrcmtOtkHdCk findEntity(String cid) {
		KrcmtOtkHdCk entity = this.queryProxy().query("SELECT v FROM KrcmtOtkHdCk v WHERE v.cid = :cid", KrcmtOtkHdCk.class)
											.setParameter("cid", cid).getSingleOrNull();
		return entity;
	}
	
	private List<KrcctOtkWtNonTarget> toNonTarget(List<WorkTypeCode> typeCode, String cid){
		return typeCode.stream().map(t -> new KrcctOtkWtNonTarget(cid, t.v())).collect(Collectors.toList());
	}
	
	private List<KrcmtOtkHdCkWktpTgt> toTarget(List<WorkTypeCode> typeCode, String cid){
		return typeCode.stream().map(t -> new KrcmtOtkHdCkWktpTgt(cid, t.v())).collect(Collectors.toList());
	}
}