package nts.uk.ctx.at.aggregation.infra.entity.scheduletable.outputsetting;

import javax.persistence.Column;
import javax.persistence.Table;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 
 * @author quytb
 *
 */

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KAGMT_RPT_SCHEDULE_TALLY_BYWKP")
public class KagmtRptScheduleTallyByWkp extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 2596984431271218289L;
	
	@EmbeddedId
	public KagmtRptScheduleTallyByWkpPk pk;

	@Column(name = "DISPORDER")
	public Integer displayOrder;

	@Override
	protected Object getKey() {
		return this.pk;
	}
	
	public String getCode() {
		return this.pk.code;
	}
}
