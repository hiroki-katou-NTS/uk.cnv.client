package nts.uk.ctx.at.record.infra.entity.monthly.erroralarm;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class KrcdtEmployeeMonthlyPerErrorPK implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name = "ERR_NO")
	public int no;

	@Column(name = "ERROR_TYPE")
	public Integer errorType;
	
	// 年月
	@Column(name = "YM")
	public Integer yearMonth;
	
	/** エラー発生社員: 社員ID*/
	@Column(name = "SID")
	public String employeeID;

	// 締めID
	@Column(name = "CLOSURE_ID")
	public Integer closureId;

	// 締め日.日
	@Column(name = "CLOSURE_DAY")
	public Integer closeDay;

	// 締め日.末日とする
	@Column(name = "IS_LAST_DAY")
	public Integer isLastDay;
	
}
