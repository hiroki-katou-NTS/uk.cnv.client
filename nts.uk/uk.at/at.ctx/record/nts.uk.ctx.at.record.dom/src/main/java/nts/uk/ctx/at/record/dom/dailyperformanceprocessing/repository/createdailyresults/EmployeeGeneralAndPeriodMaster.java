package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.ctx.at.shared.dom.adapter.generalinfo.dtoimport.EmployeeGeneralInfoImport;
import nts.uk.ctx.at.shared.dom.dailyperformanceprocessing.output.PeriodInMasterList;

/**
 * @author thanh_nx
 *
 *         マスターデータ
 */
@AllArgsConstructor
@Data
public class EmployeeGeneralAndPeriodMaster {

	// 特定期間の社員情報
	EmployeeGeneralInfoImport employeeGeneralInfoImport;

	// 期間内マスタ一覧
	PeriodInMasterList periodInMasterList;
}
