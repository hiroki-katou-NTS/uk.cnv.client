package nts.uk.ctx.at.request.app.find.application.applicationlist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDate;
@AllArgsConstructor
@Getter
public class AppGroupExportDto {
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
	 * 事前事後区分
	 */
	private int prePostAtr;
}
