package nts.uk.ctx.at.function.infra.entity.alarm.checkcondition;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.FixedConditionWorkRecord;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.FixedConditionWorkRecordName;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.WorkRecordFixedCheckItem;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.daily.KrcmtDailyAlarmCondition;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

@NoArgsConstructor
@Entity
@Table(name = "KRCMT_DAILY_ALARM_CONDITION")
public class KrcmtFixedConditionWorkRecord extends UkJpaEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KrcmtFixedConditionWorkRecordPK krcmtFixedConditionWorkRecordPK;
	
	@Column(name = "MESSAGE")
	public String message;

	@Column(name = "USE_ATR")
	public int useAtr;
	
	@ManyToOne
	@JoinColumn(name="DAILY_ALARM_CON_ID", referencedColumnName="DAILY_ALARM_CON_ID", insertable = false, updatable = false)
	public KrcmtDailyAlarmCondition dailyalarmcondition;
	
	public KrcmtFixedConditionWorkRecord(KrcmtFixedConditionWorkRecordPK krcmtFixedConditionWorkRecordPK,
			String message, int useAtr) {
		super();
		this.krcmtFixedConditionWorkRecordPK = krcmtFixedConditionWorkRecordPK;
		this.message = message;
		this.useAtr = useAtr;
	}
	
	@Override
	protected Object getKey() {
		return krcmtFixedConditionWorkRecordPK;
	}
	
	public static KrcmtFixedConditionWorkRecord toEntity(FixedConditionWorkRecord domain) {
		return new KrcmtFixedConditionWorkRecord(
				new KrcmtFixedConditionWorkRecordPK( domain.getDailyAlarmConID(),
				domain.getFixConWorkRecordNo().value),
				domain.getMessage().v(),
				domain.isUseAtr()?1:0
				);
	}
	
	public FixedConditionWorkRecord toDomain() {
		return new FixedConditionWorkRecord(
				this.krcmtFixedConditionWorkRecordPK.dailyAlarmConID,
				EnumAdaptor.valueOf(this.krcmtFixedConditionWorkRecordPK.fixConWorkRecordNo, WorkRecordFixedCheckItem.class),
				new FixedConditionWorkRecordName(this.message),
				this.useAtr == 1?true:false
				);
	}
	
	
	



}
