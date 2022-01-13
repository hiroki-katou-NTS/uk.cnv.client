package nts.uk.screen.at.app.kdw013.a;

import java.util.List;

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
	private List<Integer> supportFrameNos;
	
	// 時間帯: 時間帯
	private TimeZoneCommand timeZone;

}
