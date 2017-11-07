package nts.uk.ctx.at.record.dom.dailyperformanceprocessing;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.workrecord.log.EmpCalAndSumExeLog;
import nts.uk.ctx.at.record.dom.workrecord.log.EmpCalAndSumExeLogRepository;

@Stateless
public class CreateDailyResultDomainServiceImpl implements CreateDailyResultDomainService {

	@Inject
	private EmpCalAndSumExeLogRepository empCalAndSumExeLogRepository;
	
	@Inject
	private CreateDailyResultEmployeeDomainService createDailyResultEmployeeDomainService;

	@Override
	public int createDailyResult(List<String> emloyeeIds, int reCreateAttr, GeneralDate startDate,
			GeneralDate endDate, int executionAttr, String empCalAndSumExecLogID) {
		
		/**
		 * 正常終了 : 0
		 */
		int endStatus = 0;

		// ③日別実績の作成処理
		// 日別作成を実行するかチェックする
		Optional<EmpCalAndSumExeLog> exeLogDailyCreation = this.empCalAndSumExeLogRepository
				.getByExecutionContent(empCalAndSumExecLogID, 0);
		if (exeLogDailyCreation.isPresent()) {
			// パラメータ「実行区分」＝手動 の場合
			if (executionAttr == 0) {
				this.empCalAndSumExeLogRepository.updateLogInfo(empCalAndSumExecLogID);
			}
		} else {
			endStatus = 0;
		}
		
		emloyeeIds.forEach(f -> {
			//社員1人分の処理
			this.createDailyResultEmployeeDomainService.createDailyResultEmployee(f, startDate, endDate);
		});

		return endStatus;
	}

}
