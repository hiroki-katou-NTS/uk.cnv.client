package nts.uk.ctx.basic.infra.entity.system.bank.branch.entity.era;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.layer.infra.data.entity.type.GeneralDateToDBConverter;
import nts.arc.time.GeneralDate;

@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class CmnmtEraPk implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Basic(optional = false)
	@Column(name = "STR_D")
	@Convert(converter = GeneralDateToDBConverter.class)
	public GeneralDate startDate;
}
