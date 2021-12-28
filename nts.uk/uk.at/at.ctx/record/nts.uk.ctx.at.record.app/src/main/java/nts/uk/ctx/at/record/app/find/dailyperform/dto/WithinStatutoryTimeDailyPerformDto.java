package nts.uk.ctx.at.record.app.find.dailyperform.dto;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.attendance.util.item.AttendanceItemDataGate;
import nts.uk.ctx.at.shared.dom.common.amount.AttendanceAmountDaily;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.ItemConst;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.WithinStatutoryMidNightTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.WithinStatutoryTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeDivergenceWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ValueType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.WorkingHoursUnitPrice;

/** 日別実績の所定内時間 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WithinStatutoryTimeDailyPerformDto implements ItemConst, AttendanceItemDataGate {

	/** 就業時間: 勤怠時間 */
	@AttendanceItemLayout(layout = LAYOUT_A, jpPropertyName = WORK_TIME)
	@AttendanceItemValue(type = ValueType.TIME)
	private Integer workTime;

	/** 実働就業時間: 勤怠時間 */
	@AttendanceItemLayout(layout = LAYOUT_B, jpPropertyName = ACTUAL + WORK_TIME)
	@AttendanceItemValue(type = ValueType.TIME)
	private Integer workTimeIncludeVacationTime;

	/** 所定内割増時間: 勤怠時間 */
	@AttendanceItemLayout(layout = LAYOUT_C, jpPropertyName = PREMIUM)
	@AttendanceItemValue(type = ValueType.TIME)
	private Integer withinPrescribedPremiumTime;
	
	/** 実働所定内割増時間: 勤怠時間 */
	@AttendanceItemLayout(layout = LAYOUT_D, jpPropertyName = ACTUAL + PREMIUM)
	@AttendanceItemValue(type = ValueType.TIME)
	private Integer actualWithinPremiumTime;

	/** 所定内深夜時間: 所定内深夜時間 */
	@AttendanceItemLayout(layout = LAYOUT_E, jpPropertyName = LATE_NIGHT)
	private CalcAttachTimeDto withinStatutoryMidNightTime;

	/** 単価: 勤怠日別金額 */
	@AttendanceItemLayout(layout = LAYOUT_F, jpPropertyName = PRICE_UNIT)
	@AttendanceItemValue(type = ValueType.AMOUNT_NUM)
	private Integer unitPrice;

	/** 就業時間金額: 勤怠日別金額 */
	@AttendanceItemLayout(layout = LAYOUT_G, jpPropertyName = WORK_TIME + AMOUNT)
	@AttendanceItemValue(type = ValueType.AMOUNT_NUM)
	private Integer workTimeAmount;

	@Override
	public AttendanceItemDataGate newInstanceOf(String path) {
		if (LATE_NIGHT.equals(path)) {
			return new CalcAttachTimeDto();
		}
		return AttendanceItemDataGate.super.newInstanceOf(path);
	}

	@Override
	public Optional<AttendanceItemDataGate> get(String path) {
		if (LATE_NIGHT.equals(path)) {
			return Optional.ofNullable(withinStatutoryMidNightTime);
		}
		return AttendanceItemDataGate.super.get(path);
	}

	@Override
	public void set(String path, AttendanceItemDataGate value) {
		if (LATE_NIGHT.equals(path)) {
			withinStatutoryMidNightTime = (CalcAttachTimeDto) value;
		}
	}
	
	@Override
	public Optional<ItemValue> valueOf(String path) {
		switch (path) {
		case WORK_TIME:
			return Optional.of(ItemValue.builder().value(workTime).valueType(ValueType.TIME));
		case (ACTUAL + WORK_TIME):
			return Optional.of(ItemValue.builder().value(workTimeIncludeVacationTime).valueType(ValueType.TIME));
		case PREMIUM:
			return Optional.of(ItemValue.builder().value(withinPrescribedPremiumTime).valueType(ValueType.TIME));
		case (ACTUAL + PREMIUM):
			return Optional.of(ItemValue.builder().value(actualWithinPremiumTime).valueType(ValueType.TIME));
		case PRICE_UNIT:
			return Optional.of(ItemValue.builder().value(unitPrice).valueType(ValueType.AMOUNT_NUM));
		case (WORK_TIME + AMOUNT):
			return Optional.of(ItemValue.builder().value(workTimeAmount).valueType(ValueType.AMOUNT_NUM));
		default:
			return Optional.empty();
		}
	}

	@Override
	public void set(String path, ItemValue value) {
		switch (path) {
		case WORK_TIME:
			this.workTime = value.valueOrDefault(null);
			break;
		case (ACTUAL + WORK_TIME):
			this.workTimeIncludeVacationTime = value.valueOrDefault(null);
			break;
		case PREMIUM:
			this.withinPrescribedPremiumTime = value.valueOrDefault(null);
			break;
		case (ACTUAL + PREMIUM):
			this.actualWithinPremiumTime = value.valueOrDefault(null);
			break;
		case PRICE_UNIT:
			this.unitPrice = value.valueOrDefault(null);
			break;
		case (WORK_TIME + AMOUNT):
			this.workTimeAmount = value.valueOrDefault(null);
			break;
		default:
			break;
		}
	}
	
	@Override
	public PropType typeOf(String path) {
		switch (path) {
		case WORK_TIME:
		case (ACTUAL + WORK_TIME):
		case PREMIUM:
		case (ACTUAL + PREMIUM):
		case (HOLIDAY + ADD):
		case PRICE_UNIT:
		case (WORK_TIME + AMOUNT):
			return PropType.VALUE;
		default:
			return PropType.OBJECT;
		}
	}
	
	public static WithinStatutoryTimeDailyPerformDto fromWithinStatutoryTimeDailyPerform(
			WithinStatutoryTimeOfDaily domain) {
		return domain == null ? null: new WithinStatutoryTimeDailyPerformDto(
						getAttendanceTime(domain.getWorkTime()),
						getAttendanceTime(domain.getActualWorkTime()),
						getAttendanceTime(domain.getWithinPrescribedPremiumTime()),
						getAttendanceTime(domain.getActualWithinPremiumTime()),
						getWithStatutory(domain.getWithinStatutoryMidNightTime()),
						getWorkingHoursUnitPrice(domain.getUnitPrice()),
						getAttendanceAmount(domain.getWithinWorkTimeAmount()));
	}
	
	@Override
	public WithinStatutoryTimeDailyPerformDto clone() {
		return new WithinStatutoryTimeDailyPerformDto(workTime,
									workTimeIncludeVacationTime,
									withinPrescribedPremiumTime,
									actualWithinPremiumTime,
									withinStatutoryMidNightTime == null ? null : withinStatutoryMidNightTime.clone(),
									unitPrice.intValue(),
									workTimeAmount.intValue());
	}

	private static CalcAttachTimeDto getWithStatutory(WithinStatutoryMidNightTime domain) {
		return domain == null || domain.getTime() == null ? null : CalcAttachTimeDto.toTimeWithCal(domain.getTime());
	}

	public WithinStatutoryTimeOfDaily toDomain() {
		WithinStatutoryTimeOfDaily domain = new WithinStatutoryTimeOfDaily(
				toAttendanceTime(workTime),
				toAttendanceTime(workTimeIncludeVacationTime), 
				toAttendanceTime(withinPrescribedPremiumTime),
				toAttendanceTime(actualWithinPremiumTime),
				new WithinStatutoryMidNightTime(withinStatutoryMidNightTime == null ? TimeDivergenceWithCalculation.defaultValue()
							: withinStatutoryMidNightTime.createTimeDivWithCalc()),
				toWorkingHoursUnitPrice(unitPrice),
				toAttendanceAmount(workTimeAmount));
		return domain;
	}
	
	private static Integer getAttendanceTime(AttendanceTime domain) {
		return domain == null ? 0 : domain.valueAsMinutes();
	}
	
	private static Integer getWorkingHoursUnitPrice(WorkingHoursUnitPrice domain) {
		return domain == null ? 0 : domain.v();
	}
	
	private static Integer getAttendanceAmount(AttendanceAmountDaily domain) {
		return domain == null ? 0 : domain.v();
	}
	
	private AttendanceTime toAttendanceTime(Integer time) {
		return time == null ? AttendanceTime.ZERO : new AttendanceTime(time);
	}
	
	private WorkingHoursUnitPrice toWorkingHoursUnitPrice(Integer time) {
		return time == null ? WorkingHoursUnitPrice.ZERO : new WorkingHoursUnitPrice(time);
	}
	
	private AttendanceAmountDaily toAttendanceAmount(Integer time) {
		return time == null ? AttendanceAmountDaily.ZERO : new AttendanceAmountDaily(time);
	}
}
