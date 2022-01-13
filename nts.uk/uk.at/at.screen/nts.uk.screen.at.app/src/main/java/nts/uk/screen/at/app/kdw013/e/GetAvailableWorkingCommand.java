package nts.uk.screen.at.app.kdw013.e;

import lombok.Getter;
import nts.arc.time.GeneralDate;

/**
 * 
 * @author tutt
 *
 */
@Getter
public class GetAvailableWorkingCommand {
	// 社員ID
	private String sId;
	
	// 基準日
	private GeneralDate refDate;
	
	// 作業枠NO
	private int taskFrameNo;
	
	//上位枠作業コード
	private String taskCode;
}
