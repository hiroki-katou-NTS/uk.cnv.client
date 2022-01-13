package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults;

import java.util.Optional;

import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.ExecutionTypeDaily;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.EmpCalAndSumExeLog;
import nts.uk.ctx.at.shared.dom.adapter.generalinfo.dtoimport.EmployeeGeneralInfoImport;
import nts.uk.ctx.at.shared.dom.dailyperformanceprocessing.output.PeriodInMasterList;

/**
 * ⑤社員の日別実績を作成する
 * @author tutk
 *
 */
public interface CreateDailyResultEmployeeDomainServiceNew {

	/**
	 * 
	 * @param employeeId
	 * @param periodTimes
	 * @param companyId
	 * @param empCalAndSumExecLogID
	 * @param employeeGeneralInfoImport  特定期間の社員情報
	 * @param periodInMasterList  期間内マスタ一覧
	 * @param executionType  実行タイプ（作成する、打刻反映する、実績削除する）
	 * @param checkLock  ロック中の計算/集計できるか (true/false,checkLock == null)
	 * @return
	 */
	OutputCreateDailyResult createDailyResultEmployee(String employeeId,
			DatePeriod periodTimes, String companyId, Optional<EmpCalAndSumExeLog> empCalAndSumExeLog,
			EmployeeGeneralInfoImport employeeGeneralInfoImport,
			PeriodInMasterList periodInMasterList, ExecutionTypeDaily executionType, Optional<Boolean> checkLock);
	

	
}
