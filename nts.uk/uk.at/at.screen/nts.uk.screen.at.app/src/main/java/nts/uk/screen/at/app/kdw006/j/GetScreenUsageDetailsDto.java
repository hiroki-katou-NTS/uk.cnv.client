package nts.uk.screen.at.app.kdw006.j;

import java.util.List;

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
public class GetScreenUsageDetailsDto {

	/** 工数入力表示フォーマット */
	private GetDisplayFormatDto manHourInputDisplayFormat;

	/** 利用する勤怠項目 */
	private List<DailyAttendanceItemKDW006Dto> dailyAttendanceItem;

	/** 工数実績項目 */
	private List<AcquireManHourRecordItemsDto> manHourRecordItem;
}
