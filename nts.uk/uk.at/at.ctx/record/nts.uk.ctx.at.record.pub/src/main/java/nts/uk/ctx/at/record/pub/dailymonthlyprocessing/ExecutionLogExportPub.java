package nts.uk.ctx.at.record.pub.dailymonthlyprocessing;

import java.util.Optional;

public interface ExecutionLogExportPub {
	/**
	 * pub 承認結果反映用の設定情報
	 * @param empCalAndSumExecLogID 就業計算と集計実行ログID＝パラメータ「実行ID」
	 * @param executionContent 実行内容＝”承認結果の反映”
	 * @return
	 */
	Optional<SetInforReflAprResultExport> optReflectResult(String empCalAndSumExecLogID, int executionContent);
	/**
	 * pub ドメインモデル「就業計算と集計実行ログ」を取得
	 * @param empCalAndSumExecLogID
	 * @return
	 */
	Optional<ExeStateOfCalAndSumExport> executionStatus(String empCalAndSumExecLogID);
	/**
	 * pub ドメインモデル「対象者」を更新する
	 * @param empCalAndSumExecLogID
	 * @param executionContent
	 * @param processStatus
	 */
	void updateLogInfo(String employeeID, String empCalAndSumExecLogId, int executionContent, int state);
	
	void updateLogInfo(String empCalAndSumExecLogID, int executionContent, int processStatus);
	
	boolean isCalWhenLock(String empCalAndSumExecLogId, int executionContent);
}
