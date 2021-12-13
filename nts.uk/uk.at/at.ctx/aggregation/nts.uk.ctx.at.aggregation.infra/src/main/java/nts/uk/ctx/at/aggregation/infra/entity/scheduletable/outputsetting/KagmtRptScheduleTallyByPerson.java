package nts.uk.ctx.at.aggregation.infra.entity.scheduletable.outputsetting;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

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
@Table(name = "KAGMT_RPT_SCHEDULE_TALLY_BYPERSON")
public class KagmtRptScheduleTallyByPerson extends ContractUkJpaEntity implements Serializable {
	
	private static final long serialVersionUID = -1876740800378983233L;
	
	@EmbeddedId
	public KagmtRptScheduleTallyByPersonPk pk;

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
