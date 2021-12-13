package nts.uk.ctx.at.function.dom.alarm.sendemail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author thuongtv
 *
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValueExtractAlarmManualDto {
	private String guid;
	private String workplaceID;
	private String workplaceCode;
	private String hierarchyCd;
	private String workplaceName;
	private String employeeID;
	private String employeeCode;
	private String employeeName;
	private String alarmValueDate;
	private Integer category;
	private String categoryName;
	private String alarmItem;
	private String alarmValueMessage;
	private String comment;
	private String checkedValue;
}
