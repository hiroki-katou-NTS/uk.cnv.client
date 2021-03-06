package nts.uk.ctx.at.function.dom.alarm.checkcondition.appapproval;

import java.util.Optional;

import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;

/**
 * 申請承認の固定抽出条件
 */
@Getter
public class AppApprovalFixedExtractCondition extends AggregateRoot {

	/**
	 * エラーアラームチェックID
	 */
	private String errorAlarmCheckId;
	
	private AppApprovalFixedCheckItem no;
	
	private Optional<ErrorAlarmMessage> message;
	
	private boolean useAtr;

	
	public AppApprovalFixedExtractCondition(String errorAlarmCheckId, AppApprovalFixedCheckItem no, Optional<ErrorAlarmMessage> message,
			boolean useAtr) {
		this.errorAlarmCheckId = errorAlarmCheckId;
		this.no = no;
		this.message = message;
		this.useAtr = useAtr;
	}
}
