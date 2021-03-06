package nts.uk.ctx.at.schedule.infra.repository.schedule.alarm.workmethodrelationship;

import lombok.Value;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.uk.ctx.at.schedule.dom.schedule.alarm.workmethodrelationship.*;
import nts.uk.ctx.at.schedule.infra.entity.schedule.alarm.workmethodrelationship.*;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class JpaWorkMethodRelationshipOrg extends JpaRepository implements WorkMethodRelationshipOrgRepo{

	
	private static final String SELECT_ONE_WORK_CONTEXT = "select * from KSCMT_ALCHK_WORK_CONTEXT_ORG"
			+ " where CID = @companyId"
			+ " and TARGET_UNIT = @targetUnit"
			+ " and TARGET_ID = @targetId"
			+ " and PREVIOUS_WORK_ATR = @prevWorkMethod"
			+ " and PREVIOUS_WKTM_CD = @prevWorkTimeCode";
	
	private static final String SELECT_ONE_WORK_CONTEXT_DTL = "select * from KSCMT_ALCHK_WORK_CONTEXT_ORG_DTL"
			+ " where CID = @companyId"
			+ " and TARGET_UNIT = @targetUnit"
			+ " and TARGET_ID = @targetId"
			+ " and PREVIOUS_WORK_ATR = @prevWorkMethod"
			+ " and PREVIOUS_WKTM_CD = @prevWorkTimeCode"
			+ " order by CURRENT_WKTM_CD ASC";
	
	private static final String SELECT_LIST_WORK_CONTEXT = "select * from KSCMT_ALCHK_WORK_CONTEXT_ORG"
			+ " where CID = @companyId"
			+ " and TARGET_UNIT = @targetUnit"
			+ " and TARGET_ID = @targetId"
			+ " and PREVIOUS_WKTM_CD in @prevWorkTimeCodeList"
			+ " order by PREVIOUS_WKTM_CD ASC";
	
	private static final String SELECT_LIST_WORK_CONTEXT_DTL = "select * from KSCMT_ALCHK_WORK_CONTEXT_ORG_DTL"
			+ " where CID = @companyId"
			+ " and TARGET_UNIT = @targetUnit"
			+ " and TARGET_ID = @targetId"
			+ " and PREVIOUS_WKTM_CD = @prevWorkTimeCodeList"
			+ " order by PREVIOUS_WKTM_CD, CURRENT_WKTM_CD ASC";
	
	private static final String SELECT_ALL_WORK_CONTEXT = "select * from KSCMT_ALCHK_WORK_CONTEXT_ORG"
			+ " where CID = @companyId"
			+ " and TARGET_UNIT = @targetUnit"
			+ " and TARGET_ID = @targetId"
			+ " order by PREVIOUS_WKTM_CD ASC";
	
	private static final String SELECT_ALL_WORK_CONTEXT_DTL = "select * from KSCMT_ALCHK_WORK_CONTEXT_ORG_DTL"
			+ " where CID = @companyId"
			+ " and TARGET_UNIT = @targetUnit"
			+ " and TARGET_ID = @targetId"
			+ " order by PREVIOUS_WKTM_CD, CURRENT_WKTM_CD ASC";
	
	
	@Override
	public void insert(String companyId, WorkMethodRelationshipOrganization domain) {
		KscmtAlchkWorkContextOrg workContext = KscmtAlchkWorkContextOrg.fromDomain(companyId, domain);
		List<KscmtAlchkWorkContextOrgDtl> workContextDtlList = KscmtAlchkWorkContextOrgDtl.fromDomain(domain);
		
		this.commandProxy().insert(workContext);
		this.commandProxy().insertAll(workContextDtlList);
	}

	@Override
	public void update(String companyId, WorkMethodRelationshipOrganization domain) {
		KscmtAlchkWorkContextOrg workContext = KscmtAlchkWorkContextOrg.fromDomain(companyId, domain);
		workContext.contractCd = AppContexts.user().contractCode();
		this.commandProxy().update(workContext);

		List<KscmtAlchkWorkContextOrgDtl> workContextDtlList = KscmtAlchkWorkContextOrgDtl.fromDomain(domain);

		List<KscmtAlchkWorkContextOrgDtl> entities = getWithWorkMethodDtl(companyId, domain.getTargetOrg(), domain.getWorkMethodRelationship().getPrevWorkMethod());
		for (KscmtAlchkWorkContextOrgDtl item : entities) {
			if (workContextDtlList.stream().noneMatch(x -> KscmtAlchkWorkContextOrgDtlPk.isEquals(item.pk, x.pk))) {
				this.commandProxy().remove(KscmtAlchkWorkContextOrgDtl.class, item.pk);
			}
		}

		this.commandProxy().updateAll(workContextDtlList);

	}

	@Override
	public void deleteAll(String companyId, TargetOrgIdenInfor targetOrg) {
		Entities entities = getAllEntities(companyId, targetOrg);
		
		this.commandProxy().removeAll(entities.workContextList);
		this.commandProxy().removeAll(entities.workContextDtlList);
		
	}

	@Override
	public void deleteWorkMethod(String companyId, TargetOrgIdenInfor targetOrg, WorkMethod prevWorkMethod) {
		
		String prevWorkTimeCode = prevWorkMethod.getWorkMethodClassification() ==  WorkMethodClassfication.ATTENDANCE ?
				((WorkMethodAttendance) prevWorkMethod).getWorkTimeCode().v() : 
					KscmtAlchkWorkContextCmp.HOLIDAY_WORK_TIME_CODE;
		
		Optional<KscmtAlchkWorkContextOrg> workContext = 
				new NtsStatement(SELECT_ONE_WORK_CONTEXT, this.jdbcProxy())
				.paramString("companyId", companyId)
				.paramInt("targetUnit", targetOrg.getUnit().value)
				.paramString("targetId", targetOrg.getTargetId())
				.paramInt("prevWorkMethod", prevWorkMethod.getWorkMethodClassification().value)
				.paramString("prevWorkTimeCode", prevWorkTimeCode)
				.getSingle(KscmtAlchkWorkContextOrg.mapper);
		
		if (!workContext.isPresent()) {
			return;
		}
		
		List<KscmtAlchkWorkContextOrgDtl> workContextDtlList = 
				new NtsStatement(SELECT_ONE_WORK_CONTEXT_DTL, this.jdbcProxy())
				.paramString("companyId", companyId)
				.paramInt("targetUnit", targetOrg.getUnit().value)
				.paramString("targetId", targetOrg.getTargetId())
				.paramInt("prevWorkMethod", prevWorkMethod.getWorkMethodClassification().value)
				.paramString("prevWorkTimeCode", prevWorkTimeCode)
				.getList( KscmtAlchkWorkContextOrgDtl.mapper);
		
		this.commandProxy().remove(KscmtAlchkWorkContextOrg.class,workContext.get().pk);
		this.commandProxy().removeAll(KscmtAlchkWorkContextOrgDtl.class, workContextDtlList.stream().map(i -> i.pk).collect(Collectors.toList()));
	}

	@Override
	public List<WorkMethodRelationshipOrganization> getAll(String companyId, TargetOrgIdenInfor targetOrg) {
		
		Entities entities = getAllEntities(companyId, targetOrg);
		
		return toDomain(entities.workContextList, entities.workContextDtlList);
	}

	@Override
	public Optional<WorkMethodRelationshipOrganization> getWithWorkMethod(String companyId, TargetOrgIdenInfor targetOrg,
			WorkMethod prevWorkMethod) {
		String prevWorkTimeCode = prevWorkMethod.getWorkMethodClassification() ==  WorkMethodClassfication.ATTENDANCE ?
				((WorkMethodAttendance) prevWorkMethod).getWorkTimeCode().v() : 
					KscmtAlchkWorkContextCmp.HOLIDAY_WORK_TIME_CODE;
		
		Optional<KscmtAlchkWorkContextOrg> workContext = 
				new NtsStatement(SELECT_ONE_WORK_CONTEXT, this.jdbcProxy())
				.paramString("companyId", companyId)
				.paramInt("targetUnit", targetOrg.getUnit().value)
				.paramString("targetId", targetOrg.getTargetId())
				.paramInt("prevWorkMethod", prevWorkMethod.getWorkMethodClassification().value)
				.paramString("prevWorkTimeCode", prevWorkTimeCode)
				.getSingle(KscmtAlchkWorkContextOrg.mapper);
		
		if (!workContext.isPresent()) {
			return Optional.empty();
		}
		
		List<KscmtAlchkWorkContextOrgDtl> workContextDtlList = 
				new NtsStatement(SELECT_ONE_WORK_CONTEXT_DTL, this.jdbcProxy())
				.paramString("companyId", companyId)
				.paramInt("targetUnit", targetOrg.getUnit().value)
				.paramString("targetId", targetOrg.getTargetId())
				.paramInt("prevWorkMethod", prevWorkMethod.getWorkMethodClassification().value)
				.paramString("prevWorkTimeCode", prevWorkTimeCode)
				.getList( KscmtAlchkWorkContextOrgDtl.mapper);
		
		WorkMethodRelationshipOrganization domain = toDomain(Arrays.asList(workContext.get()), workContextDtlList).get(0);
		return Optional.of(domain);
	}

	@Override
	public List<WorkMethodRelationshipOrganization> getWithWorkMethodList(String companyId, TargetOrgIdenInfor targetOrg,
			List<WorkMethod> prevWorkMethodList) {
		
		List<String> prevWorkTimeCodeList = prevWorkMethodList.stream().map( p -> 
		  p.getWorkMethodClassification() ==  WorkMethodClassfication.ATTENDANCE? 
					((WorkMethodAttendance) p).getWorkTimeCode().v() : 
					KscmtAlchkWorkContextCmp.HOLIDAY_WORK_TIME_CODE
		).collect(Collectors.toList());
		
		List<KscmtAlchkWorkContextOrg> workContextList = 
				new NtsStatement(SELECT_LIST_WORK_CONTEXT, this.jdbcProxy())
				.paramString("companyId", companyId)
				.paramInt("targetUnit", targetOrg.getUnit().value)
				.paramString("targetId", targetOrg.getTargetId())
				.paramString("prevWorkTimeCodeList", prevWorkTimeCodeList)
				.getList( KscmtAlchkWorkContextOrg.mapper);
		
		List<KscmtAlchkWorkContextOrgDtl> workContextDtlList = 
				new NtsStatement(SELECT_LIST_WORK_CONTEXT_DTL, this.jdbcProxy())
				.paramString("prevWorkTimeCodeList", prevWorkTimeCodeList)
				.paramString("companyId", companyId)
				.paramInt("targetUnit", targetOrg.getUnit().value)
				.paramString("targetId", targetOrg.getTargetId())
				.getList( KscmtAlchkWorkContextOrgDtl.mapper);
		
		return toDomain(workContextList, workContextDtlList);
	}

	@Override
	public boolean exists(String companyId, TargetOrgIdenInfor targetOrg, WorkMethod prevWorkMethod) {
		
		String prevWorkTimeCode = prevWorkMethod.getWorkMethodClassification() ==  WorkMethodClassfication.ATTENDANCE?
				((WorkMethodAttendance) prevWorkMethod).getWorkTimeCode().v() : 
					KscmtAlchkWorkContextCmp.HOLIDAY_WORK_TIME_CODE;
				
		return this.queryProxy().find(
									new KscmtAlchkWorkContextOrgPk(
										companyId, 
										targetOrg.getUnit().value, 
										targetOrg.getTargetId(), 
										prevWorkMethod.getWorkMethodClassification().value, 
										prevWorkTimeCode), 
									KscmtAlchkWorkContextOrg.class)
								.isPresent();
		
	}
	
	private List<WorkMethodRelationshipOrganization> toDomain(List<KscmtAlchkWorkContextOrg> workContextList, 
			List<KscmtAlchkWorkContextOrgDtl> workContextDtlList) {
		
		return workContextList.stream().map( wContext -> {
			
			List<KscmtAlchkWorkContextOrgDtl> dtlList = 
					workContextDtlList.stream()
					.filter( dtl -> dtl.pk.prevWorkTimeCode.equals(wContext.pk.prevWorkTimeCode))
					.collect(Collectors.toList());
			
			return wContext.toDomain(dtlList);
		
		}).collect(Collectors.toList());
		
	}
	
	private Entities getAllEntities(String companyId, TargetOrgIdenInfor targetOrg) {
			
		List<KscmtAlchkWorkContextOrg> workContextList = 
				new NtsStatement(SELECT_ALL_WORK_CONTEXT, this.jdbcProxy())
				.paramString("companyId", companyId)
				.paramInt("targetUnit", targetOrg.getUnit().value)
				.paramString("targetId", targetOrg.getTargetId())
				.getList( KscmtAlchkWorkContextOrg.mapper);
		
		List<KscmtAlchkWorkContextOrgDtl> workContextDtlList = 
				new NtsStatement(SELECT_ALL_WORK_CONTEXT_DTL, this.jdbcProxy())
				.paramString("companyId", companyId)
				.paramInt("targetUnit", targetOrg.getUnit().value)
				.paramString("targetId", targetOrg.getTargetId())
				.getList( KscmtAlchkWorkContextOrgDtl.mapper);
		
		return new Entities(workContextList, workContextDtlList); 
	}

	private List<KscmtAlchkWorkContextOrgDtl> getWithWorkMethodDtl(String companyId, TargetOrgIdenInfor targetOrg,
																   WorkMethod prevWorkMethod) {
		String prevWorkTimeCode = prevWorkMethod.getWorkMethodClassification() == WorkMethodClassfication.ATTENDANCE ?
				((WorkMethodAttendance) prevWorkMethod).getWorkTimeCode().v() :
				KscmtAlchkWorkContextCmp.HOLIDAY_WORK_TIME_CODE;

		List<KscmtAlchkWorkContextOrgDtl> workContextDtlList =
				new NtsStatement(SELECT_ONE_WORK_CONTEXT_DTL, this.jdbcProxy())
						.paramString("companyId", companyId)
						.paramInt("targetUnit", targetOrg.getUnit().value)
						.paramString("targetId", targetOrg.getTargetId())
						.paramInt("prevWorkMethod", prevWorkMethod.getWorkMethodClassification().value)
						.paramString("prevWorkTimeCode", prevWorkTimeCode)
						.getList(KscmtAlchkWorkContextOrgDtl.mapper);

		return workContextDtlList;
	}

	@Value
	class Entities {
		
		List<KscmtAlchkWorkContextOrg> workContextList;
		
		List<KscmtAlchkWorkContextOrgDtl> workContextDtlList; 
	}

}
