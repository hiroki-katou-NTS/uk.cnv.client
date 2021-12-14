package nts.uk.ctx.at.record.pub.workrecord.actuallock.export;

import lombok.Getter;
import nts.arc.time.calendar.period.DatePeriod;

/**
 *  所属雇用履歴 
 *  */
@Getter
public class EmploymentHistoryExport {

	/** The employee id. */
	// 社員ID
	private String employeeId;

	/** The job title code. */
	// 雇用コード
	private String employmentCode;

	/** The period. */
	// 配属期間
	private DatePeriod period;

	public EmploymentHistoryExport(String employeeId, String employmentCode, DatePeriod period) {
		super();
		this.employeeId = employeeId;
		this.employmentCode = employmentCode;
		this.period = period;
	}

}
