package nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.condition;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.StartEndClassificationShare;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * @author thanh_nx
 * 
 *         申請から反映する時刻
 */
@AllArgsConstructor
@Getter
public class TimeReflectFromApp {

	// 勤務NO
	private WorkNo workNo;

	// 開始終了区分
	private StartEndClassificationShare startEndClassification;

	// 時刻
	private TimeWithDayAttr timeOfDay;
	
	// 勤務場所
	private Optional<WorkLocationCD> workLocationCd;

}
