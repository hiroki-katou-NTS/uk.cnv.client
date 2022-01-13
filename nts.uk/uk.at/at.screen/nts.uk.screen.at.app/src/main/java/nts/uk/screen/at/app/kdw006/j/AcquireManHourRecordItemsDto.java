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
public class AcquireManHourRecordItemsDto {

	/** 項目ID*/
	private int itemId;
	
	/** 名称*/
	private String name;
	
	/** フォーマット設定に表示する*/
	private int useAtr;
	
}
