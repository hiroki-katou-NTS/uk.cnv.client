/**
 * 
 */
package nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing;

import java.util.Optional;

import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ErrorPresent;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionStatus;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionContent;

/**
 * 実行ログ
 * @author tutk
 *
 */
@Getter
public class ExecutionLog extends AggregateRoot {
	
	/** 就業計算と集計実行ログID */
	private String empCalAndSumExecLogID;
	
	/** 実行内容 */
	private ExecutionContent executionContent;
	
	/** エラーの有無 */
	private ErrorPresent existenceError;
	
	/**
	 * 実行日時
	 * start time - end time
	 */
	private ExecutionTime executionTime;
	
	/**
	 * 処理状況
	 */
	private ExecutionStatus processStatus;

	/**
	 * 対象期間
	 * start date - end date
	 */
	private Optional<ObjectPeriod> objectPeriod;
	
    private Optional<Boolean> isCalWhenLock;
	
	/** 計算実行設定情報ID */	
	public String getCalExecutionSetInfoID() {
		if (executionContent == ExecutionContent.DAILY_CREATION) {
			return dailyCreationSetInfo.get().getCalExecutionSetInfoID();
		} else if (executionContent == ExecutionContent.DAILY_CALCULATION) {
			return dailyCalSetInfo.get().getCalExecutionSetInfoID();
		} else if (executionContent == ExecutionContent.REFLRCT_APPROVAL_RESULT) {
			return reflectApprovalSetInfo.get().getCalExecutionSetInfoID();
		} else {
			return monlyAggregationSetInfo.get().getCalExecutionSetInfoID();
		}
	}
	
	/** 承認結果反映の設定情報 */
	private Optional<SetInforReflAprResult> reflectApprovalSetInfo;
	public void setReflectApprovalSetInfo(SetInforReflAprResult reflectApprovalSetInfo) {
		this.reflectApprovalSetInfo = Optional.of(reflectApprovalSetInfo);
	}
	
	/** 日別作成の設定情報 */
	private Optional<SettingInforForDailyCreation> dailyCreationSetInfo;
	public void setDailyCreationSetInfo(SettingInforForDailyCreation dailyCreationSetInfo) {
		this.dailyCreationSetInfo = Optional.of(dailyCreationSetInfo);
	}
	
	/** 日別計算の設定情報 */
	private Optional<CalExeSettingInfor> dailyCalSetInfo;
	public void setDailyCalSetInfo(CalExeSettingInfor dailyCalSetInfo) {
		this.dailyCalSetInfo = Optional.of(dailyCalSetInfo);
	}
	
	/** 月別集計の設定情報 */
	private Optional<CalExeSettingInfor> monlyAggregationSetInfo;
	public void setMonlyAggregationSetInfo(CalExeSettingInfor monlyAggregationSetInfo) {
		this.monlyAggregationSetInfo = Optional.of(monlyAggregationSetInfo);
	}
	
	public ExecutionLog(String empCalAndSumExecLogID, ExecutionContent executionContent, ErrorPresent existenceError,
			ExecutionTime executionTime, ExecutionStatus processStatus, ObjectPeriod objectPeriod, Optional<Boolean> isCalWhenLock) {
		super();
		this.empCalAndSumExecLogID = empCalAndSumExecLogID;
		this.executionContent = executionContent;
		this.existenceError = existenceError;
		this.executionTime = executionTime;
		this.processStatus = processStatus;
		this.objectPeriod = Optional.ofNullable(objectPeriod);
		this.reflectApprovalSetInfo = Optional.empty();
		this.dailyCreationSetInfo =  Optional.empty();
		this.dailyCalSetInfo =  Optional.empty();
		this.monlyAggregationSetInfo =  Optional.empty();
		this.isCalWhenLock = isCalWhenLock;
	}
	
	public static ExecutionLog createFromJavaType (
			String empCalAndSumExecLogID,
			int executionContent,
			int existenceError,
			GeneralDateTime startExecutionTime,
			GeneralDateTime endExecutionTime,
			int processStatus,
			GeneralDate startObjectPeriod,
			GeneralDate endObjectPeriod,
			Optional<Boolean> isCalWhenLock) {
		return new ExecutionLog(
				empCalAndSumExecLogID,
				EnumAdaptor.valueOf(executionContent,ExecutionContent.class),
				EnumAdaptor.valueOf(existenceError,ErrorPresent.class),
				new ExecutionTime(startExecutionTime,endExecutionTime),
				EnumAdaptor.valueOf(processStatus,ExecutionStatus.class),
				new ObjectPeriod(startObjectPeriod,endObjectPeriod),
				isCalWhenLock);
	}
	
	public boolean isComplete() {
		return this.processStatus == ExecutionStatus.DONE;
	}
	
}
