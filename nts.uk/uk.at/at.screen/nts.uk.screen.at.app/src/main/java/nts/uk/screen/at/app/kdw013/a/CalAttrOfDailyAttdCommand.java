package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calcategory.CalAttrOfDailyAttd;

@AllArgsConstructor
@Getter
public class CalAttrOfDailyAttdCommand {
	// フレックス超過時間: フレックス超過時間の自動計算設定
	private AutoCalFlexOvertimeSettingCommand flexExcessTime;

	// 加給: 加給の自動計算設定
	private AutoCalRaisingSalarySettingCommand rasingSalarySetting;

	// 休出時間: 休出時間の自動計算設定
	private AutoCalRestTimeSettingCommand holidayTimeSetting;

	// 残業時間: 残業時間の自動計算設定
	private AutoCalOvertimeSettingCommand overtimeSetting;

	// 遅刻早退: 遅刻早退の自動計算設定
	private AutoCalcOfLeaveEarlySettingCommand leaveEarlySetting;

	// 乖離時間: 乖離時間の自動計算設定
	private AutoCalcSetOfDivergenceTimeCommand divergenceTime;

	public CalAttrOfDailyAttd toDomain() {
		return new CalAttrOfDailyAttd(
				this.getFlexExcessTime().toDomain(), 
				this.getRasingSalarySetting().toDomain(), 
				this.getHolidayTimeSetting().toDomain(), 
				this.getOvertimeSetting().toDomain(),
				this.getLeaveEarlySetting().toDomain(), 
				this.getDivergenceTime().toDomain()
				);
	}
}
