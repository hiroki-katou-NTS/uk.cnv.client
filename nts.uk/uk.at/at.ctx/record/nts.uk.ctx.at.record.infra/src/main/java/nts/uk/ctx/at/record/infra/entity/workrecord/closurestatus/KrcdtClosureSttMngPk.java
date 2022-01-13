package nts.uk.ctx.at.record.infra.entity.workrecord.closurestatus;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * @author HungTT
 *
 */

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class KrcdtClosureSttMngPk {

	// 年月
	@Column(name = "YM")
	public int yearMonth;

	// 社員ID
	@Column(name = "SID")
	public String employeeId;

	// 締めID
	@Column(name = "CLOSURE_ID")
	public int closureId;

	// 締め日
	@Column(name = "CLOSURE_DAY")
	public int closeDay;

	// 締め日
	@Column(name = "IS_LAST_DAY")
	public boolean isLastDay;

}
