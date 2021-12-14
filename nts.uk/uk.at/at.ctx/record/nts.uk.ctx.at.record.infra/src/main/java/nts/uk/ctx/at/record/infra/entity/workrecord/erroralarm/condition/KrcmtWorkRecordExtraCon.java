package nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition;

import java.io.Serializable;

import javax.persistence.Column;
//import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.DisplayMessages;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.WorkRecordExtractingCondition;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.enums.TypeCheckWorkRecord;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue.ColorCode;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue.NameWKRecord;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;
/**
 *勤務実績の固定抽出条件 
 *
 */
@NoArgsConstructor
@Entity
@Table(name = "KRCMT_ALST_CHKDAY_UD")
public class KrcmtWorkRecordExtraCon  extends ContractUkJpaEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "ERROR_ALARM_CHECK_ID")
	public String errorAlarmCheckID;
	
	@Column(name = "CHECK_ITEM")
	public int checkItem;
	@Column(name = "MESSAGE_BOLD")
	public int messageBold;
	
	@Column(name = "MESSAGE_COLOR")
	public String messageColor;
	
	@Column(name = "SORT_ORDER_BY")
	public int sortOrderBy;
	
	@Column(name = "USE_ATR")
	public int useAtr;
	
	@Column(name = "NAME")
	public String nameWKRecord;
	
	@Override
	protected Object getKey() {
		return errorAlarmCheckID;
	}

	public KrcmtWorkRecordExtraCon(String errorAlarmCheckID, int checkItem, int messageBold, String messageColor,
			int sortOrderBy, int useAtr, String nameWKRecord) {
		super();
		this.errorAlarmCheckID = errorAlarmCheckID;
		this.checkItem = checkItem;
		this.messageBold = messageBold;
		this.messageColor = messageColor;
		this.sortOrderBy = sortOrderBy;
		this.useAtr = useAtr;
		this.nameWKRecord = nameWKRecord;
	}
	
	public static KrcmtWorkRecordExtraCon toEntity(WorkRecordExtractingCondition domain) {
		return new KrcmtWorkRecordExtraCon(
				domain.getErrorAlarmCheckID(),
				domain.getCheckItem().value,
				domain.getDisplayMessages().isMessageBold()?1:0,
				domain.getDisplayMessages().getMessageColor().v(),
				domain.getSortOrderBy(),
				domain.isUseAtr()?1:0,
				domain.getNameWKRecord().v()
				);
	}
	
	public WorkRecordExtractingCondition toDomain() {
		return new WorkRecordExtractingCondition(
				this.errorAlarmCheckID,
				EnumAdaptor.valueOf(this.checkItem, TypeCheckWorkRecord.class),
				new DisplayMessages(
					this.messageBold == 1?true:false,
					new ColorCode(this.messageColor)),
				this.sortOrderBy,
				this.useAtr == 1?true:false,
				new NameWKRecord(this.nameWKRecord)
				);
	}

	
}
