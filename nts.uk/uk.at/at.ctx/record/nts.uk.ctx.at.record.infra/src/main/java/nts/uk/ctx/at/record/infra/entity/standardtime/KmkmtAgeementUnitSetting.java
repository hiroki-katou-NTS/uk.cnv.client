package nts.uk.ctx.at.record.infra.entity.standardtime;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="KRCMT_36AGR_UNIT")
public class KmkmtAgeementUnitSetting extends UkJpaEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
    public KmkmtAgeementUnitSettingPK kmkmtAgeementUnitSettingPK;
	
	@Column(name ="EMPLOYMENT_USE_ATR")
	public BigDecimal employmentUseAtr;
	
	@Column(name ="WORKPLACE_USE_ATR")
	public BigDecimal workPlaceUseAtr;
	
	@Column(name ="CLASSIFICATION_USE_ATR")
	public BigDecimal classificationUseAtr;
	
	@Override
	protected Object getKey() {
		return this.kmkmtAgeementUnitSettingPK;
	}
}
