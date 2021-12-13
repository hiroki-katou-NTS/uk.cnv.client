package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrorMessageInfo;

/**
 * 
 * @author tutt
 *
 */
@Setter
@Getter
@AllArgsConstructor
public class ErrorMessageInfoDto {
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
	private int executionContent;
	/**
	 * リソースID
	 */
	private String resourceID;
	/**
	 * エラーメッセージ
	 */
	private String messageError;

	public static ErrorMessageInfoDto toDto(ErrorMessageInfo domain) {
		return new ErrorMessageInfoDto(domain.getCompanyID(), domain.getEmployeeID(), domain.getProcessDate(),
				domain.getExecutionContent().value, domain.getResourceID().v(), domain.getMessageError().v());

	}
}
