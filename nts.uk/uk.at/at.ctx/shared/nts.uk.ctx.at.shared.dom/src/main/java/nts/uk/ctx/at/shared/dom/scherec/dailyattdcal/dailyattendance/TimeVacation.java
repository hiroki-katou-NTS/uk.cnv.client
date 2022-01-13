package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance;

import java.util.List;

import lombok.Value;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;

/**
 * 時間休暇
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared(勤務予定、勤務実績).日の勤怠計算.日別勤怠.時間休暇
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
