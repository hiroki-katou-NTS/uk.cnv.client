package nts.uk.screen.at.app.kdw006.k;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetManHourRecordItemSpecifiedIDListDto {

	/** 項目ID*/
	private int itemId;
	
	/** 名称*/
	private String name;
	
	/** フォーマット設定に表示する*/
	private boolean useAtr;
	
}
