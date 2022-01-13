package nts.uk.screen.at.app.kdw013.a;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import nts.arc.error.BusinessException;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DataResultAfterIU;

/**
 * 
 * @author tutt
 *
 */
@Setter
@Getter
public class RegisterWorkContentDto {

	// List<残業休出時間>
	private List<OvertimeLeaveTimeDto> lstOvertimeLeaveTime;
	
	private DataResultAfterIU dataResult;
	
	private List<BusinessException> alarmMsg_2081;
}
