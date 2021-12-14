package nts.uk.ctx.at.schedule.dom.schedule.workschedule;

import java.util.List;

import lombok.Value;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.TimevacationUseTimeOfDaily;

/**
 * 時間休暇
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務予定.勤務予定.勤務予定.勤務予定.時間休暇
 * @author lan_lt
 *
 */

@Value
public class TimeVacation{
	
	/** 時間帯リスト */
	private List<TimeSpanForCalc> timeList;

	/** 使用時間 */
	private TimevacationUseTimeOfDaily useTime;


}
