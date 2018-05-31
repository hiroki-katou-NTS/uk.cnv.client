package nts.uk.ctx.at.function.infra.entity.monthlycorrection.fixedformatmonthly;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class KrcmtMonthlyRecordWorkTypePK implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column(name = "CID")
	public String companyID;
	
	@Column(name = "BUSINESS_TYPE_CODE")
	public String businessTypeCode;

}
