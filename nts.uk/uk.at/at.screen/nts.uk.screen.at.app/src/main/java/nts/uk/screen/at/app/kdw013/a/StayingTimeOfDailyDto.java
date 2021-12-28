package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.StayingTimeOfDaily;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StayingTimeOfDailyDto {
	/** PCログオフ後時間: 勤怠時間 */
	private Integer afterPCLogOffTime;

	/** PCログオン前時間: 勤怠時間 */
	private Integer beforePCLogOnTime;

	/** 出勤前時間: 勤怠時間 */
	private Integer beforeWoringTime;

	/** 滞在時間: 勤怠時間 */
	private Integer stayingTime;

	/** 退勤後時間: 勤怠時間 */
	private Integer afterLeaveTime;

	public static StayingTimeOfDailyDto fromDomain(StayingTimeOfDaily domain) {
		return new StayingTimeOfDailyDto(
				domain.getAfterPCLogOffTime().v(), 
				domain.getBeforePCLogOnTime().v(), 
				domain.getBeforeWoringTime().v(), 
				domain.getStayingTime().v(),
				domain.getAfterLeaveTime().v());
	}
}
