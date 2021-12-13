package nts.uk.screen.at.ws.kdw.kdw013;

import lombok.Data;
import nts.arc.time.GeneralDate;

/**
 * 
 * @author tutt
 *
 */
@Data
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
}
