package nts.uk.ctx.at.shared.infra.entity.holidaysetting.employee.carryForwarddata;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class KshdtHdpubRemHistPK implements Serializable{

	private static final long serialVersionUID = 1L;

	/** 社員ID */
    @Column(name = "SID")
    public String sid;

	/** 年月 */
	@Column(name = "YM")
	@Basic(optional = false)
	public int yearMonth;

	/** 締めID */
	@Column(name = "CLOSURE_ID")
	@Basic(optional = false)
	public int closureId;

	/** 締め日.日 */
	@Column(name = "CLOSURE_DAY")
	@Basic(optional = false)
	public int closeDay;

	/** 締め日.末日とする */
	@Column(name = "IS_LAST_DAY")
	@Basic(optional = false)
	public boolean isLastDay;
}
