package nts.uk.ctx.at.shared.app.command.scherec.dailyattendanceitem;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.ControlOfAttendanceItems;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.primitivevalue.HeaderBackgroundColor;

@Getter
@Setter
@NoArgsConstructor
public class ControlOfAttendanceItemsCmd {

	/**	会社ID*/
	private String companyID;
	
	/**	勤怠項目ID*/
	private int itemDailyID;
	
	/**	日別実績のヘッダ背景色*/
	private String headerBgColorOfDailyPer;
	
	private UpdateDailyAttendanceItemCommand updateDailyAttendanceItemCommand;
	
	public static ControlOfAttendanceItems fromDomain(ControlOfAttendanceItemsCmd command) {
		return new ControlOfAttendanceItems(
				command.getCompanyID(),
				command.getItemDailyID(),
				command.getHeaderBgColorOfDailyPer() == null ? null : new HeaderBackgroundColor(command.getHeaderBgColorOfDailyPer())
		);
	}
}
