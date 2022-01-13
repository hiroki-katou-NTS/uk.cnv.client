package nts.uk.ctx.sys.shared.dom.employee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.calendar.period.DatePeriod;
/**
 * 社員の在籍期間Imported
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.権限管理.Imported.contexts.社員.社員の在籍期間Imported
 * @author lan_lt
 */
@Getter
@AllArgsConstructor
public class EmpEnrollPeriodImport {
	/** 社員ID **/
	private final String empID;

	/** 期間 (Period) **/
	private final DatePeriod datePeriod;
	
	/** 出向状況 (Enum) **/
	private final SecondSituation secondedSituation;
}
