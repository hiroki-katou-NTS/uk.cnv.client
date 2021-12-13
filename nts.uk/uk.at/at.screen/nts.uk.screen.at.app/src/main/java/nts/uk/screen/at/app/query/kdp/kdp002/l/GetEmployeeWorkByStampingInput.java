package nts.uk.screen.at.app.query.kdp.kdp002.l;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author chungnt
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetEmployeeWorkByStampingInput {

	// 社員ID
	private String sid;
	
	// 作業枠NO
	private int workFrameNo;
	
	// 上位枠作業コード
	private String upperFrameWorkCode;
	
}
