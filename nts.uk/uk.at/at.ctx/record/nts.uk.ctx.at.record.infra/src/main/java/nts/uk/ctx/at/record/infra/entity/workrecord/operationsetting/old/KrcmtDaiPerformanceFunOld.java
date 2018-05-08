package nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting.old;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import nts.uk.shr.infra.data.entity.UkJpaEntity;

@Entity
@Table(name = "KRCMT_DAI_PERFORMANCE_FUN_OLD")
public class KrcmtDaiPerformanceFunOld extends UkJpaEntity{
	
	@Id
	@Column(name = "FUNCTION_NO")
	public BigDecimal functionNo;
	
	@Column(name = "DESCRIPTION_OF_FUNCTION")
	public String descriptionOfFunction;

	@Column(name = "DISPLAY_NAME_OF_FUNCTION")
	public String displayNameOfFunction;
	
	@Override
	protected Object getKey() {
		return this.functionNo;
	}
	
}
