package nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.monthlycheckcondition;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.FixedConditionWorkRecordName;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.monthlycheckcondition.FixedExtraMon;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.monthlycheckcondition.SysFixedMonPerEral;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@NoArgsConstructor
@Entity
@Table(name = "KRCMT_ALST_CHKMON_FIXED")
public class KrcmtFixedExtraMon extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	public KrcmtFixedExtraMonPK krcmtFixedExtraMonPK;
	
	@Column(name = "USE_ATR")
	public int useAtr;
	
	@Column(name = "MESSAGE")
	public String message;
	
	
	@Override
	protected Object getKey() {
		return krcmtFixedExtraMonPK;
	}

	public KrcmtFixedExtraMon(KrcmtFixedExtraMonPK krcmtFixedExtraMonPK, int useAtr, String message) {
		super();
		this.krcmtFixedExtraMonPK = krcmtFixedExtraMonPK;
		this.useAtr = useAtr;
		this.message = message;
	}
	
	
	public static KrcmtFixedExtraMon toEntity(FixedExtraMon domain) {
		return new KrcmtFixedExtraMon(
				new KrcmtFixedExtraMonPK(
					domain.getMonAlarmCheckID(),
					domain.getFixedExtraItemMonNo().value),
				domain.isUseAtr()?1:0,
				!domain.getMessage().isPresent()?null:domain.getMessage().get().v()
				);
		
	}
	
	public FixedExtraMon toDomain() {
		return new FixedExtraMon(
				this.krcmtFixedExtraMonPK.monAlarmCheckID,
				EnumAdaptor.valueOf(this.krcmtFixedExtraMonPK.fixedExtraItemMonNo, SysFixedMonPerEral.class),
				this.useAtr==0?false:true,
				new FixedConditionWorkRecordName(this.message)
				);
		
	}





}
