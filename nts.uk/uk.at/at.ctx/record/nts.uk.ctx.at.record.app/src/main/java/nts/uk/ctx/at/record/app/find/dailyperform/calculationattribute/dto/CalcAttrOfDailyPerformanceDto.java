package nts.uk.ctx.at.record.app.find.dailyperform.calculationattribute.dto;

import lombok.Data;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.calculationattribute.AutoCalcSetOfDivergenceTime;
import nts.uk.ctx.at.record.dom.calculationattribute.CalAttrOfDailyPerformance;
import nts.uk.ctx.at.record.dom.calculationattribute.enums.DivergenceTimeAttr;
import nts.uk.ctx.at.record.dom.calculationattribute.enums.LeaveAttr;
import nts.uk.ctx.at.shared.app.util.attendanceitem.ConvertHelper;
import nts.uk.ctx.at.shared.dom.attendance.util.ItemConst;
import nts.uk.ctx.at.shared.dom.attendance.util.anno.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.attendance.util.anno.AttendanceItemRoot;
import nts.uk.ctx.at.shared.dom.attendance.util.anno.AttendanceItemValue;
import nts.uk.ctx.at.shared.dom.attendance.util.item.AttendanceItemCommon;
import nts.uk.ctx.at.shared.dom.attendance.util.item.ValueType;
import nts.uk.ctx.at.shared.dom.ot.autocalsetting.AutoCalAtrOvertime;
import nts.uk.ctx.at.shared.dom.ot.autocalsetting.AutoCalFlexOvertimeSetting;
import nts.uk.ctx.at.shared.dom.ot.autocalsetting.AutoCalOvertimeSetting;
import nts.uk.ctx.at.shared.dom.ot.autocalsetting.AutoCalRestTimeSetting;
import nts.uk.ctx.at.shared.dom.ot.autocalsetting.AutoCalSetting;
import nts.uk.ctx.at.shared.dom.ot.autocalsetting.AutoCalcOfLeaveEarlySetting;
import nts.uk.ctx.at.shared.dom.ot.autocalsetting.TimeLimitUpperLimitSetting;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.AutoCalRaisingSalarySetting;

@Data
@AttendanceItemRoot(rootName = ItemConst.DAILY_CALCULATION_ATTR_NAME)
public class CalcAttrOfDailyPerformanceDto extends AttendanceItemCommon {

	/** 社員ID: 社員ID */
	private String employeeId;

	/** 年月日: 年月日 */
	private GeneralDate ymd;

	/** フレックス超過時間: フレックス超過時間の自動計算設定 */
	@AttendanceItemLayout(layout = LAYOUT_A, jpPropertyName = FLEX)
	private AutoCalculationSettingDto flexExcessTime;

	/** 加給: 加給の自動計算設定 */
	@AttendanceItemLayout(layout = LAYOUT_B, jpPropertyName = RAISING_SALARY)
	private AutoCalRaisingSalarySettingDto rasingSalarySetting;

	/** 休出時間: 休出時間の自動計算設定 */
	@AttendanceItemLayout(layout = LAYOUT_C, jpPropertyName = HOLIDAY_WORK)
	private AutoCalHolidaySettingDto holidayTimeSetting;

	/** 残業時間: 残業時間の自動計算設定 */
	@AttendanceItemLayout(layout = LAYOUT_D, jpPropertyName = OVERTIME)
	private AutoCalOfOverTimeDto overtimeSetting;

	/** 遅刻早退: 遅刻早退の自動計算設定 */
	@AttendanceItemLayout(layout = LAYOUT_E, jpPropertyName = LATE + LEAVE_EARLY)
	private AutoCalOfLeaveEarlySettingDto leaveEarlySetting;

	/** 乖離時間: 乖離時間の自動計算設定 */
	@AttendanceItemLayout(layout = LAYOUT_F, jpPropertyName = DIVERGENCE)
	@AttendanceItemValue(type = ValueType.ATTR)
	private int divergenceTime;
	
	public static CalcAttrOfDailyPerformanceDto getDto(CalAttrOfDailyPerformance domain) {
		CalcAttrOfDailyPerformanceDto result = new CalcAttrOfDailyPerformanceDto();
		if (domain != null) {
			result.setEmployeeId(domain.getEmployeeId());
			result.setYmd(domain.getYmd());
			result.setDivergenceTime(getDivergence(domain.getDivergenceTime()));
			result.setFlexExcessTime(newAutoCalcSetting(domain.getFlexExcessTime().getFlexOtTime()));
			result.setHolidayTimeSetting(newAutoCalcHolidaySetting(domain.getHolidayTimeSetting()));
			result.setLeaveEarlySetting(newAutoCalcLeaveSetting(domain.getLeaveEarlySetting()));
			result.setOvertimeSetting(getOverTimeSetting(domain.getOvertimeSetting()));
			result.setRasingSalarySetting(newAutoCalcSalarySetting(domain.getRasingSalarySetting()));
			result.setYmd(domain.getYmd());
			result.exsistData();
		}
		return result;
	}

	private static int getDivergence(AutoCalcSetOfDivergenceTime domain) {
		return domain == null || domain.getDivergenceTime() == null ? 0 : domain.getDivergenceTime().value;
	}

	private static AutoCalRaisingSalarySettingDto newAutoCalcSalarySetting(AutoCalRaisingSalarySetting domain) {
		return domain == null ? null : new AutoCalRaisingSalarySettingDto(
						domain.isRaisingSalaryCalcAtr() ? 1 : 0,
						domain.isSpecificRaisingSalaryCalcAtr() ? 1 : 0);
	}

	private static AutoCalOfLeaveEarlySettingDto newAutoCalcLeaveSetting(AutoCalcOfLeaveEarlySetting autoCalcOfLeaveEarlySetting) {
		return autoCalcOfLeaveEarlySetting == null ? null : new AutoCalOfLeaveEarlySettingDto(
						autoCalcOfLeaveEarlySetting.isLeaveEarly() ? 1 : 0,
						autoCalcOfLeaveEarlySetting.isLate() ? 1 : 0 
						);
	}

	private static AutoCalHolidaySettingDto newAutoCalcHolidaySetting(AutoCalRestTimeSetting domain) {
		return domain == null ? null : new AutoCalHolidaySettingDto(
						newAutoCalcSetting(domain.getRestTime()),
						newAutoCalcSetting(domain.getLateNightTime()));
	}

	private static AutoCalOfOverTimeDto getOverTimeSetting(AutoCalOvertimeSetting domain) {
		return domain == null ? null : new AutoCalOfOverTimeDto(
						newAutoCalcSetting(domain.getEarlyOtTime()),
						newAutoCalcSetting(domain.getEarlyMidOtTime()),
						newAutoCalcSetting(domain.getNormalOtTime()),
						newAutoCalcSetting(domain.getNormalMidOtTime()),
						newAutoCalcSetting(domain.getLegalOtTime()),
						newAutoCalcSetting(domain.getLegalMidOtTime()));
	}

	private static AutoCalculationSettingDto newAutoCalcSetting(AutoCalSetting domain) {
		return domain == null ? null : new AutoCalculationSettingDto(
					domain.getCalAtr() == null ?  0 : domain.getCalAtr().value, 
					domain.getUpLimitORtSet() == null ? 0 : domain.getUpLimitORtSet().value);
	}

	@Override
	public String employeeId() {
		return this.employeeId;
	}

	@Override
	public GeneralDate workingDate() {
		return this.ymd;
	}

	@Override
	public CalAttrOfDailyPerformance toDomain(String employeeId, GeneralDate date) {

		if(!this.isHaveData()) {
			return null;
		}
		if (employeeId == null) {
			employeeId = this.employeeId();
		}
		if (date == null) {
			date = this.workingDate();
		}
		return new CalAttrOfDailyPerformance(
				employeeId,  date, 
				new AutoCalFlexOvertimeSetting(newAutoCalcSetting(this.flexExcessTime)),
				createAutoCalcRaisingSalarySetting(),
				createAutoCalcHolidaySetting(),
				createAutoOverTimeSetting(),
				createAutoCalcLeaveSetting(),
				new AutoCalcSetOfDivergenceTime(getEnum(this.divergenceTime, DivergenceTimeAttr.class)));
	}

	private AutoCalRaisingSalarySetting createAutoCalcRaisingSalarySetting() {
		return this.rasingSalarySetting == null ? null : new AutoCalRaisingSalarySetting(
						this.rasingSalarySetting.getSalaryCalSetting() == 1 ? true : false,
						this.rasingSalarySetting.getSpecificSalaryCalSetting() == 1 ? true: false);
	}

	private AutoCalRestTimeSetting createAutoCalcHolidaySetting() {
		return this.holidayTimeSetting == null ? null : new AutoCalRestTimeSetting(
				newAutoCalcSetting(this.holidayTimeSetting.getHolidayWorkTime()),
				newAutoCalcSetting(this.holidayTimeSetting.getLateNightTime()));
	}

	private AutoCalcOfLeaveEarlySetting createAutoCalcLeaveSetting() {
		return this.leaveEarlySetting == null ? null : new AutoCalcOfLeaveEarlySetting(
				this.leaveEarlySetting.getLeaveLate() == 1 ? true : false,
				this.leaveEarlySetting.getLeaveEarly() == 1 ? true : false);
	}

	private AutoCalOvertimeSetting createAutoOverTimeSetting() {
		return this.overtimeSetting == null ? null : new AutoCalOvertimeSetting(
				newAutoCalcSetting(this.overtimeSetting.getEarlyOverTime()),
				newAutoCalcSetting(this.overtimeSetting.getEarlyMidnightOverTime()),
				newAutoCalcSetting(this.overtimeSetting.getNormalOverTime()),
				newAutoCalcSetting(this.overtimeSetting.getNormalMidnightOverTime()),
				newAutoCalcSetting(this.overtimeSetting.getLegalOverTime()),
				newAutoCalcSetting(this.overtimeSetting.getLegalMidnightOverTime()));
	}
	
	private AutoCalSetting newAutoCalcSetting(AutoCalculationSettingDto dto) {
		return dto == null ? null : new AutoCalSetting(
												getEnum(dto.getUpperLimitSetting(), TimeLimitUpperLimitSetting.class),
												getEnum(dto.getCalculationAttr(), AutoCalAtrOvertime.class));
	}

	private <T> T getEnum(int value, Class<T> enumType) {
		return ConvertHelper.getEnum(value, enumType);
	}
}
