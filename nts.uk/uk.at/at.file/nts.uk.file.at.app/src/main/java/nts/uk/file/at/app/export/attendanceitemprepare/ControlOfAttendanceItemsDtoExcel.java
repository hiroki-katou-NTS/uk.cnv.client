package nts.uk.file.at.app.export.attendanceitemprepare;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
/**
 * 
 * @author Hoidd 
 *
 */
@Setter
@Getter
@AllArgsConstructor
public class ControlOfAttendanceItemsDtoExcel {

	/**勤怠項目ID*/
	private int itemDailyID;
	
	/**日別実績のヘッダ背景色*/
	private String headerBgColorOfDailyPer;

	/**時間項目の入力単位*/
//	private Float inputUnitOfTimeItem;
	
}
