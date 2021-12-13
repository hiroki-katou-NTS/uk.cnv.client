package nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.mastercheck;

import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.BooleanUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.mastercheck.ErrorAlarmMessageMSTCHK;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.mastercheck.MasterCheckFixedCheckItem;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.mastercheck.MasterCheckFixedExtractCondition;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

@Entity
@Table(name = "KRCMT_MSTCHK_FIXEDEXTRCON")
@NoArgsConstructor
@Getter @Setter
public class KrcmtMasterCheckFixedExtractCondition extends UkJpaEntity {

	@EmbeddedId
	private KrcmtMasterCheckFixedExtractConditionPK pk;
	
	@Column(name = "CONTRACT_CD")
	private String contractCode;
	
	@Column(name = "MESSAGE_DIS")
	private String message;
	
	@Column(name = "USE_ATR")
	private boolean useAtr;
	
	public KrcmtMasterCheckFixedExtractCondition(KrcmtMasterCheckFixedExtractConditionPK pk, String contractCode,
			String message, int useAtr) {
		this.pk = pk;
		this.contractCode = contractCode;
		this.message = message;
		this.useAtr = BooleanUtils.toBoolean(useAtr);
	}
	
	@Override
	protected Object getKey() {
		return pk;
	}
	
	public static KrcmtMasterCheckFixedExtractCondition toEntity(MasterCheckFixedExtractCondition domain) {
		return new KrcmtMasterCheckFixedExtractCondition(
				new KrcmtMasterCheckFixedExtractConditionPK(domain.getErrorAlarmCheckId(), domain.getNo().value),
				AppContexts.user().contractCode(),
				domain.getMessage().isPresent() ? domain.getMessage().get().v() : "",
				domain.isUseAtr()?1:0
				);
	}
	
	public MasterCheckFixedExtractCondition toDomain() {
		return new MasterCheckFixedExtractCondition(
					this.pk.getErAlId(),
					EnumAdaptor.valueOf(this.pk.getNo(), MasterCheckFixedCheckItem.class),
					Optional.ofNullable(new ErrorAlarmMessageMSTCHK(this.message)),
					this.useAtr
				);
	}
}
