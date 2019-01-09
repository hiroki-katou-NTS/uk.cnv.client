package nts.uk.file.at.app.export.roledaily;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ControlOfAttendanceItemsDtoExcel {

	/**勤怠項目ID*/
	private int itemDailyID;
	
	/**日別実績のヘッダ背景色*/
	private String headerBgColorOfDailyPer;

	/**時間項目の入力単位*/
	private Integer inputUnitOfTimeItem;
}
