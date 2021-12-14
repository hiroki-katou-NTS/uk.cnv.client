package nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing;


import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.dailyperformanceprocessing.ErrMessageResource;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionContent;
/**
 * エラーメッセージ
 * @author tutk
 *
 */
@Getter
@NoArgsConstructor
public class ErrorMessageInfo  {
	
	/**
	 * 会社ID
	 */
	private String companyID;
	/**
	 * 社員ID
	 */
	private String employeeID;
	/**
	 * 処理日
	 */
	private GeneralDate processDate;
	/**
	 * 実施内容
	 */
	private ExecutionContent executionContent;
	/**
	 * リソースID
	 */
	private ErrMessageResource resourceID;
	/**
	 * エラーメッセージ
	 */
	private ErrMessageContent messageError;
	
	public ErrorMessageInfo(String companyID, String employeeID, GeneralDate processDate,
			ExecutionContent executionContent, ErrMessageResource resourceID, ErrMessageContent messageError) {
		super();
		this.companyID = companyID;
		this.employeeID = employeeID;
		this.processDate = processDate;
		this.executionContent = executionContent;
		this.resourceID = resourceID;
		this.messageError = messageError;
	}
	
	
}
