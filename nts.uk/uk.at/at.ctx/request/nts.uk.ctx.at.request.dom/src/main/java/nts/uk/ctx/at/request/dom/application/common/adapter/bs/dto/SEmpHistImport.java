package nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.calendar.period.DatePeriod;
/**
 * @author loivt
 * 所属雇用履歴を取得
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SEmpHistImport {
	
	/** The employee id. */
	// 社員ID
	private String employeeId;

	/** The job title code. */
	// 雇用コード
	private String employmentCode;

	/** The job title name. */
	// 雇用名称
	private String employmentName;

	/** The period. */
	// 配属期間 
	private DatePeriod period;
}
