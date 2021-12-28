package nts.uk.screen.at.app.kdw006.k;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;

/**
 * 
 * @author chungnt
 *
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AcquireSelectionHistoryOfWorkDto {

	/** 項目ID */
	private int itemId;

	/** 履歴項目 */
	private List<DateHistoryItemDto> dateHistoryItems;

}

@AllArgsConstructor
@NoArgsConstructor
@Data
class DateHistoryItemDto {
	
	private String historyId;
	
	private GeneralDate startDate;
	
	private GeneralDate endDate;
	
}
