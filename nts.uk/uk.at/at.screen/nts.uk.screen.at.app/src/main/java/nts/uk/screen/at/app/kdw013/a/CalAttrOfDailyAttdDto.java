package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.app.command.ot.autocalsetting.AutoCalOvertimeSettingDto;
import nts.uk.ctx.at.shared.app.command.ot.autocalsetting.AutoCalRaisingSalarySettingDto;
import nts.uk.ctx.at.shared.app.command.ot.autocalsetting.AutoCalRestTimeSettingDto;
import nts.uk.ctx.at.shared.app.command.ot.autocalsetting.AutoCalcOfLeaveEarlySettingDto;
import nts.uk.ctx.at.shared.app.command.ot.autocalsetting.wkp.AutoCalFlexOvertimeSettingDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calcategory.CalAttrOfDailyAttd;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CalAttrOfDailyAttdDto {
	// フレックス超過時間: フレックス超過時間の自動計算設定
	private AutoCalFlexOvertimeSettingDto flexExcessTime;

	// 加給: 加給の自動計算設定
	private AutoCalRaisingSalarySettingDto rasingSalarySetting;

	// 休出時間: 休出時間の自動計算設定
	private AutoCalRestTimeSettingDto holidayTimeSetting;

	// 残業時間: 残業時間の自動計算設定
	private AutoCalOvertimeSettingDto overtimeSetting;

	// 遅刻早退: 遅刻早退の自動計算設定
	private AutoCalcOfLeaveEarlySettingDto leaveEarlySetting;

	// 乖離時間: 乖離時間の自動計算設定
	private AutoCalcSetOfDivergenceTimeDto divergenceTime;

	public static CalAttrOfDailyAttdDto fromDomain(CalAttrOfDailyAttd domain) {

		return new CalAttrOfDailyAttdDto(
				AutoCalFlexOvertimeSettingDto.fromDomain(domain.getFlexExcessTime()),
				AutoCalRaisingSalarySettingDto.fromDomain(domain.getRasingSalarySetting()),
				AutoCalRestTimeSettingDto.fromDomain(domain.getHolidayTimeSetting()),
				AutoCalOvertimeSettingDto.fromDomain(domain.getOvertimeSetting()),
				AutoCalcOfLeaveEarlySettingDto.fromDomain(domain.getLeaveEarlySetting()),
				AutoCalcSetOfDivergenceTimeDto.fromDomain(domain.getDivergenceTime()));
	}
}
