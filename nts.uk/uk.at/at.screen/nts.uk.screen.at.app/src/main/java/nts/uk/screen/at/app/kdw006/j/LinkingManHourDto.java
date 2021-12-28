package nts.uk.screen.at.app.kdw006.j;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author chungnt
 *
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LinkingManHourDto {

	/** 応援勤務枠No*/
	private int frameNo;
	
	/** 工数実績項目ID*/
	private int itemId;
	
	/** 勤怠項目ID*/
	private int attendanceItemId;
	
}
