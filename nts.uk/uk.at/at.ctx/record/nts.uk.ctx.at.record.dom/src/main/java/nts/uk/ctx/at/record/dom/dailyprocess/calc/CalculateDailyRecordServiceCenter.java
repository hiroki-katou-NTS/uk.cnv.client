package nts.uk.ctx.at.record.dom.dailyprocess.calc;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import nts.arc.layer.app.command.AsyncCommandHandlerContext;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.CreateDailyResultDomainServiceImpl.ProcessState;
import nts.uk.ctx.at.record.dom.workrecord.closurestatus.ClosureStatusManagement;
import nts.uk.shr.com.time.calendar.period.DatePeriod;

/**
 * 実績計算を呼び出す用のサービス
 * @author keisuke_hoshina
 *
 */
public interface CalculateDailyRecordServiceCenter{

	//計算
	public List<IntegrationOfDaily> calculate(List<IntegrationOfDaily> integrationOfDaily);

	//計算(会社共通のマスタを渡せる場合)
	default public List<IntegrationOfDaily> calculatePassCompanySetting(List<IntegrationOfDaily> integrationOfDaily,Optional<ManagePerCompanySet> companySet) {
		return calculatePassCompanySetting(CalculateOption.asDefault(), integrationOfDaily, companySet);
	}

	//計算(会社共通のマスタを渡せる場合)
	public List<IntegrationOfDaily> calculatePassCompanySetting(CalculateOption calcOption, List<IntegrationOfDaily> integrationOfDaily,Optional<ManagePerCompanySet> companySet);
	
	//計算(就業計算と集計用)
	public CalcStatus calculateForManageState(List<IntegrationOfDaily> integrationOfDaily,Optional<AsyncCommandHandlerContext> asyncContext, Optional<Consumer<ProcessState>> counter, List<ClosureStatusManagement> closureList);
	
	//エラーチェック
	public List<IntegrationOfDaily> errorCheck(List<IntegrationOfDaily> integrationList);

	//計算(更新処理自動実行用)
	public CalcStatus calculateForclosure(List<IntegrationOfDaily> integrationOfDaily,ManagePerCompanySet companySet, List<ClosureStatusManagement> closureList);
	
}
