package nts.uk.ctx.at.request.dom.application.stamp;
/**
 * Refactor4
 * @author hoangnd
 *
 */


import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.request.dom.application.gobackdirectly.primitive.WorkLocationCD;
import nts.uk.shr.com.time.TimeWithDayAttr;
//打刻申請時刻
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TimeStampApp {
//	反映先
	DestinationTimeApp destinationTimeApp;
//	時刻
	private TimeWithDayAttr timeOfDay;
//	勤務場所
	private Optional<WorkLocationCD> workLocationCd; 
//	外出理由
	private Optional<AppStampGoOutAtr> appStampGoOutAtr;
	
}
