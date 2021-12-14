package nts.uk.ctx.at.schedulealarm.infra.repository.alarmcheck;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.uk.ctx.at.schedulealarm.dom.alarmcheck.AlarmCheckConditionSchedule;
import nts.uk.ctx.at.schedulealarm.dom.alarmcheck.AlarmCheckConditionScheduleCode;
import nts.uk.ctx.at.schedulealarm.dom.alarmcheck.AlarmCheckConditionScheduleRepository;
import nts.uk.ctx.at.schedulealarm.dom.alarmcheck.AlarmCheckMessage;
import nts.uk.ctx.at.schedulealarm.dom.alarmcheck.AlarmCheckMsgContent;
import nts.uk.ctx.at.schedulealarm.dom.alarmcheck.SubCode;
import nts.uk.ctx.at.schedulealarm.dom.alarmcheck.SubCondition;
import nts.uk.ctx.at.schedulealarm.infra.entity.alarmcheck.KscctAlchkCategory;
import nts.uk.ctx.at.schedulealarm.infra.entity.alarmcheck.KscctAlchkCategorySub;
import nts.uk.ctx.at.schedulealarm.infra.entity.alarmcheck.KscmtAlchkMessage;
import nts.uk.ctx.at.schedulealarm.infra.entity.alarmcheck.KscmtAlchkMessagePk;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class JpaAlarmCheckConditionScheduleRepository extends JpaRepository implements AlarmCheckConditionScheduleRepository{	
	// SEL BY CID
	private static final String SEL_ALL_CTG_BY_CONTRACT_CD =  "SELECT * FROM KSCCT_ALCHK_CATEGORY "
			+ " WHERE CONTRACT_CD = @contractCd"; /** -> ra 1 list condition code*/
	
	private static final String SEL_ALL_CTG_SUB_BY_CONTRACT_CD =  "SELECT * FROM KSCCT_ALCHK_CATEGORY_SUB "
			+ " WHERE CONTRACT_CD = @contractCd"; /** -> ra 1 list category sub code hay list msg default*/
	
	private static final String SEL_ALL_MSG_BY_CID = "SELECT * FROM KSCMT_ALCHK_MESSAGE "
			+ " WHERE CID = @cid"; /** -> ra 1 list msg user setting*/
	
	//SEL BY CID AND CD
	private static final String SEL_CTG_BY_CONTRACT_CD_AND_CD = "SELECT * FROM KSCCT_ALCHK_CATEGORY"
			+ " WHERE CONTRACT_CD = @contractCd"
			+ " AND CD = @code"
			+ " ORDER BY DISPORDER ASC"; /** -> ra 1 condition*/
	
	private static final String SEL_CTG_SUB_BY_CONTRACT_CD_AND_CD = "SELECT * FROM KSCCT_ALCHK_CATEGORY_SUB "
			+ " WHERE CONTRACT_CD = @contractCd"
			+ " AND CD = @code　"
			+ " ORDER BY DISPORDER ASC"; /** -> ra 1 condition*/
	
	private static final String SEL_MSG_BY_CID_AND_CD = "SELECT * FROM KSCMT_ALCHK_MESSAGE "
			+ " WHERE CID = @cid"
			+ " AND CD = @code";
	
	@Override
	public void insert(String cid, AlarmCheckConditionSchedule domain) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(String cid, AlarmCheckConditionSchedule domain) {
		this.commandProxy().updateAll(toEntityMessage(cid, domain));
	}

	@Override
	public AlarmCheckConditionSchedule get(String contractCd, String cid, AlarmCheckConditionScheduleCode alarmCode) {		
		KscctAlchkCategory ctgEntity = new NtsStatement(SEL_CTG_BY_CONTRACT_CD_AND_CD, this.jdbcProxy())
				.paramString("contractCd", contractCd)
				.paramString("code", alarmCode.v())
				.getSingle(c -> KscctAlchkCategory.MAPPER.toEntity(c)).orElse(null);
		
		List<KscctAlchkCategorySub> ctgSubEntities = new NtsStatement(SEL_CTG_SUB_BY_CONTRACT_CD_AND_CD, this.jdbcProxy())
				.paramString("contractCd", contractCd)
				.paramString("code", alarmCode.v())
				.getList(c -> KscctAlchkCategorySub.MAPPER.toEntity(c));
		
		List<KscmtAlchkMessage> msgEntities = new NtsStatement(SEL_MSG_BY_CID_AND_CD, this.jdbcProxy())
				.paramString("cid", cid)
				.paramString("code", alarmCode.v())
				.getList(c -> KscmtAlchkMessage.MAPPER.toEntity(c));
		
		return toDomain(ctgEntity, ctgSubEntities, msgEntities);
	}

	@Override
	public List<AlarmCheckConditionSchedule> getAll(String contractCd, String cid) {
		List<KscctAlchkCategory> ctgEntities = new NtsStatement(SEL_ALL_CTG_BY_CONTRACT_CD, this.jdbcProxy())
				.paramString("contractCd", contractCd)
				.getList(c -> KscctAlchkCategory.MAPPER.toEntity(c));
		
		List<KscctAlchkCategorySub> ctgSubEntities = new NtsStatement(SEL_ALL_CTG_SUB_BY_CONTRACT_CD, this.jdbcProxy())
				.paramString("contractCd", contractCd)
				.getList(c -> KscctAlchkCategorySub.MAPPER.toEntity(c));
		
		List<KscmtAlchkMessage> msgEntities = new NtsStatement(SEL_ALL_MSG_BY_CID, this.jdbcProxy())
				.paramString("cid", cid)
				.getList(c -> KscmtAlchkMessage.MAPPER.toEntity(c));
		
		return ctgEntities.stream().map(ctg -> {
			val ctgSubs = ctgSubEntities.stream().filter(sub -> sub.pk.code.equals(ctg.pk.code))
					.collect(Collectors.toList());
			val messages = msgEntities.stream().filter(msg -> msg.pk.code.equals(ctg.pk.code))
					.collect(Collectors.toList());
			
			return toDomain(ctg, ctgSubs, messages);
			
		}).collect(Collectors.toList());
	}
	
	private List<KscmtAlchkMessage> toEntityMessage(String cid, AlarmCheckConditionSchedule domain) {
		List<KscmtAlchkMessage> result = domain.getSubConditions().stream().map(c ->{
			val pk = new KscmtAlchkMessagePk(cid, domain.getCode().v(), c.getSubCode().v());
			KscmtAlchkMessage kscmtAlchkMessage = new KscmtAlchkMessage(pk, c.getMessage().getMessage().v());
			kscmtAlchkMessage.setContractCd(AppContexts.user().contractCode());
			return kscmtAlchkMessage;
		}).collect(Collectors.toList());
		return result;
	}
	
	private AlarmCheckConditionSchedule toDomain(KscctAlchkCategory ctgEntity,
			List<KscctAlchkCategorySub> ctgSubEntities, List<KscmtAlchkMessage> msgEntities) {
		if(ctgEntity==null){
			return null;
		}
		/** サブ条件リストを作る */
		List<SubCondition> subConditionLst = ctgSubEntities.stream().map( sub ->{
			KscmtAlchkMessage msg = msgEntities.stream().filter(m -> m.pk.subCode.equals(sub.pk.subCode)).findFirst().orElse(new KscmtAlchkMessage());
			return new SubCondition(new SubCode(sub.pk.subCode), 
					new AlarmCheckMsgContent(new AlarmCheckMessage(sub.defaultMsg), 
						                     new AlarmCheckMessage(msg.message)), 
					sub.explanation);
		}).collect(Collectors.toList());
		
		return new AlarmCheckConditionSchedule(new AlarmCheckConditionScheduleCode(ctgEntity.pk.code), ctgEntity.name, 
				ctgEntity.medicalOp == 1? true: false, subConditionLst);
	}
}
