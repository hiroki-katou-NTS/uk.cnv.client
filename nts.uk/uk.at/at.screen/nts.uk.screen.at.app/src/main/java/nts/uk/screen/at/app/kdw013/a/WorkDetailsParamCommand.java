package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 
 * @author tutt
 *
 */
@AllArgsConstructor
@Getter
public class WorkDetailsParamCommand {
	// 応援勤務枠No: 応援勤務枠No
	private int supportFrameNo;
	
	// 時間帯: 時間帯
	private TimeZoneCommand timeZone;
	// 作業グループ
	private WorkGroupCommand workGroup;
	
	// 備考: 作業入力備考
	private String remarks;
	
	// 勤務場所: 勤務場所コード
	private String workLocationCD;

}
