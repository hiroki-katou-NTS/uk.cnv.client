package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import nts.arc.layer.app.command.AsyncCommandHandlerContext;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.ExecutionTypeDaily;
import nts.uk.ctx.at.record.dom.organization.EmploymentHistoryImported;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.EmpCalAndSumExeLog;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.ExecutionLog;
import nts.uk.ctx.at.shared.dom.adapter.generalinfo.dtoimport.EmployeeGeneralInfoImport;
import nts.uk.ctx.at.shared.dom.calculationsetting.StampReflectionManagement;
import nts.uk.ctx.at.shared.dom.dailyperformanceprocessing.output.PeriodInMasterList;
import nts.uk.ctx.at.shared.dom.scherec.closurestatus.ClosureStatusManagement;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.shr.com.history.DateHistoryItem;

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
	 * @param reCreateWorkType  勤務種別変更時に再作成
	 * @param reCreateWorkPlace  異動時に再作成
	 * @param reCreateRestTime  休職・休業者再作成
	 * @param employeeGeneralInfoImport  特定期間の社員情報
	 * @param stampReflectionManagement
	 * @param mapWorkingConditionItem
	 * @param mapDateHistoryItem
	 * @param periodInMasterList  期間内マスタ一覧
	 * @param executionType  実行タイプ（作成する、打刻反映する、実績削除する）
	 * @param checkLock  ロック中の計算/集計できるか (true/false,checkLock == null)
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	OutputCreateDailyResult createDailyResultEmployee(AsyncCommandHandlerContext asyncContext, String employeeId,
			DatePeriod periodTimes, String companyId, Optional<EmpCalAndSumExeLog> empCalAndSumExeLog,
			boolean reCreateWorkType, boolean reCreateWorkPlace, boolean reCreateRestTime,
			EmployeeGeneralInfoImport employeeGeneralInfoImport,
			Optional<StampReflectionManagement> stampReflectionManagement,
			Map<String, Map<String, WorkingConditionItem>> mapWorkingConditionItem,
			Map<String, Map<String, DateHistoryItem>> mapDateHistoryItem, PeriodInMasterList periodInMasterList,
			ExecutionTypeDaily executionType, Optional<Boolean> checkLock);
	

	
}
