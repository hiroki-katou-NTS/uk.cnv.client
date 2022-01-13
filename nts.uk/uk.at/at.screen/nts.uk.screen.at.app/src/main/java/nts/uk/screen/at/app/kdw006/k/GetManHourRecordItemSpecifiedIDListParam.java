package nts.uk.screen.at.app.kdw006.k;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author chungnt
 *
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GetManHourRecordItemSpecifiedIDListParam {

	// 会社ID
	public String cId;
	
	// 項目リスト
	public List<Integer> items;
	
}
