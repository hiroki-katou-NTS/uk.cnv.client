package nts.uk.ctx.at.record.dom.monthlyprocess.aggr;

import nts.arc.task.data.TaskDataSetter;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ErrorPresent;
import nts.uk.ctx.at.shared.dom.dailyperformanceprocessing.ErrMessageResource;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrMessageContent;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrMessageInfo;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionContent;
import nts.uk.shr.com.i18n.TextResource;

/**
 * ドメインサービス：月別集計エラー処理
 * @author shuichi_ishida
 */
public class MonthlyAggregationErrorService {

	/**
	 * エラー処理（排他エラー用）
	 * @param dataSetter データセッター
	 * @param employeeId 社員ID
	 * @param empCalAndSumExecLogID 就業計算と集計実行ログID
	 * @param outYmd 出力年月日
	 */
	public static AtomTask errorProcForOptimisticLock(RequireM1 require,
			TaskDataSetter dataSetter,
			String employeeId,
			String empCalAndSumExecLogID,
			GeneralDate outYmd){
		
		// 「エラーあり」に更新
		dataSetter.updateData("monthlyAggregateHasError", ErrorPresent.HAS_ERROR.nameId);
		
		// エラー出力
		return AtomTask.of(() -> require.add(new ErrMessageInfo(
				employeeId,
				empCalAndSumExecLogID,
				new ErrMessageResource("024"),
				ExecutionContent.MONTHLY_AGGREGATION,
				outYmd,
				new ErrMessageContent(TextResource.localize("Msg_1541")))));
	}
	
	public static interface RequireM1 {
		
		void add(ErrMessageInfo errMessageInfo);
	}
}
