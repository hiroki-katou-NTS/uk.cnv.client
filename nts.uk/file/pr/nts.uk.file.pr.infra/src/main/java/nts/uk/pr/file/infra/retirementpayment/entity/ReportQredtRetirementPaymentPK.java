package nts.uk.pr.file.infra.retirementpayment.entity;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;

import lombok.Data;
import nts.arc.layer.infra.data.entity.type.GeneralDateToDBConverter;
import nts.arc.time.GeneralDate;

/**
 * @author hungnm
 *
 */
@Data
@Embeddable
public class ReportQredtRetirementPaymentPK implements Serializable {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The company code. */
	@Basic(optional = false)
	@Column(name = "CCD")
	private String ccd;
	
	/** The person id. */
	@Basic(optional = false)
	@Column(name = "PID")
	private String pid;
	
	@Column(name = "PAY_DATE")
	@Convert(converter = GeneralDateToDBConverter.class)
	public GeneralDate payDate;
}
