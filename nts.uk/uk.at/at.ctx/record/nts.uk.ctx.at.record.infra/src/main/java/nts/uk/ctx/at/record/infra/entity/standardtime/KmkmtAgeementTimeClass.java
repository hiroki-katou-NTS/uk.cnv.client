package nts.uk.ctx.at.record.infra.entity.standardtime;

import java.io.Serializable;

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
@Table(name = "	KMKMT_AGREEMENTTIME_CLASS")
public class KmkmtAgeementTimeClass extends UkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KmkmtAgeementTimeClassPK kmkmtAgeementTimeClassPK;

	@Column(name = "LABOR_SYSTEM_ATR")
	public int laborSystemAtr;
	
	@Column(name = "UPPER_MONTH")
	public int upperMonth;
	
	@Column(name = "UPPER_MONTH_AVERAGE")
	public int upperMonthAverage;

	@Override
	protected Object getKey() {
		return this.kmkmtAgeementTimeClassPK;
	}
}
