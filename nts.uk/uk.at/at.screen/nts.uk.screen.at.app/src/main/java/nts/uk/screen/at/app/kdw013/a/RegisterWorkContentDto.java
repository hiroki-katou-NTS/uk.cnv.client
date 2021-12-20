package nts.uk.screen.at.app.kdw013.a;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;

/**
 * 
 * @author tutt
 *
 */
@Setter
@Getter
public class RegisterWorkContentDto {
	
	// エラー一覧
	private List<ErrorMessageInfoDto> lstErrorMessageInfo;

	// List<残業休出時間>
	private List<OvertimeLeaveTime> lstOvertimeLeaveTime;
	
}
