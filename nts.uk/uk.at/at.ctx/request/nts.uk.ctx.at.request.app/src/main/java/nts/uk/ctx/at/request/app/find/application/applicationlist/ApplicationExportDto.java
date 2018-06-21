package nts.uk.ctx.at.request.app.find.application.applicationlist;

import lombok.Data;
import nts.arc.time.GeneralDate;
/**
 * @author loivt
 * ApplicationExportDto
 */
@Data

public class ApplicationExportDto {
	/**
	 * appDate,申請日
	 */
	private GeneralDate appDate;
	/**
	 * appType,申請種類
	 */
	private Integer appType;
	/**
	 * employeeID,申請者
	 */
	private String employeeID;
	/**
	 * appTypeName,申請表示名※
	 */
	private String appTypeName;
	
	/**
	 * 反映状態
	 */
	private Integer reflectState;
}
