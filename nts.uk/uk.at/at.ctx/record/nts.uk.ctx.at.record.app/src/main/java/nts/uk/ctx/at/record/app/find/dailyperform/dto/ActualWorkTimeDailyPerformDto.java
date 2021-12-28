package nts.uk.ctx.at.record.app.find.dailyperform.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.app.find.dailyperform.editstate.EditStateOfDailyPerformanceDto;
import nts.uk.ctx.at.shared.app.util.attendanceitem.ConvertHelper;
import nts.uk.ctx.at.shared.dom.attendance.util.item.AttendanceItemDataGate;
import nts.uk.ctx.at.shared.dom.common.amount.AttendanceAmountDaily;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeOfExistMinus;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.ItemConst;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ValueType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.deviationtime.DiverdenceReasonCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.deviationtime.DivergenceReasonContent;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.deviationtime.DivergenceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.deviationtime.DivergenceTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTimeOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.ActualWorkingTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.ConstraintTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.TotalWorkingTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.ExtraTimeItemNo;

/** 日別実績の勤務実績時間 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualWorkTimeDailyPerformDto implements ItemConst, AttendanceItemDataGate {

	/** 割増時間: 日別実績の割増時間 */
	@AttendanceItemLayout(layout = LAYOUT_A, jpPropertyName = PREMIUM)
	private PremiumTimeOfDailyPerformDto premiumTime;

	/** 拘束差異時間: 勤怠時間 */
	@AttendanceItemLayout(layout = LAYOUT_B, jpPropertyName = RESTRAINT + DIFF)
	@AttendanceItemValue(type = ValueType.TIME)
	private Integer constraintDifferenceTime;

	/** 拘束時間: 総拘束時間 */
	@AttendanceItemLayout(layout = LAYOUT_C, jpPropertyName = RESTRAINT)
	private ConstraintTimeDto constraintTime;

	/** 時差勤務時間: 勤怠時間 */
	@AttendanceItemLayout(layout = LAYOUT_D, jpPropertyName = TIME_DIFF + WORKING_TIME)
	@AttendanceItemValue(type = ValueType.TIME)
	private Integer timeDifferenceWorkingHours;

	/** 総労働時間: 日別実績の総労働時間 */
	@AttendanceItemLayout(layout = LAYOUT_E, jpPropertyName = TOTAL_LABOR)
	private TotalWorkingTimeDto totalWorkingTime;

	/** 乖離時間: 日別実績の乖離時間 */
	@AttendanceItemLayout(layout = LAYOUT_F, jpPropertyName = DIVERGENCE, listMaxLength = 10, indexField = DEFAULT_INDEX_FIELD_NAME)
	private List<DivergenceTimeDto> divergenceTime;

	@Override
	public ActualWorkTimeDailyPerformDto clone() {
		return new ActualWorkTimeDailyPerformDto(
						premiumTime == null ? null : premiumTime.clone(),
						constraintDifferenceTime,
						constraintTime == null ? null : constraintTime.clone(),
						timeDifferenceWorkingHours,
						totalWorkingTime == null ? null : totalWorkingTime.clone(),
						divergenceTime == null ? null : divergenceTime.stream().map(t -> t.clone()).collect(Collectors.toList()));
	}

	public static ActualWorkTimeDailyPerformDto toActualWorkTime(ActualWorkingTimeOfDaily domain) {
		return domain == null ? null : new ActualWorkTimeDailyPerformDto(
						PremiumTimeOfDailyPerformDto.toDto(domain.getPremiumTimeOfDailyPerformance()),
						getAttendanceTime(domain.getConstraintDifferenceTime()),
						getConstraintTime(domain.getConstraintTime()),
						getAttendanceTime(domain.getTimeDifferenceWorkingHours()),
						TotalWorkingTimeDto.fromTotalWorkingTime(domain.getTotalWorkingTime()),
						getDivTime(domain.getDivTime()));
	}

	private static List<DivergenceTimeDto> getDivTime(DivergenceTimeOfDaily domain) {
		return domain == null ? new ArrayList<>()
				: ConvertHelper.mapTo(domain.getDivergenceTime(), d -> DivergenceTimeDto.fromDivergenceTime(d));
	}

	private static Integer getAttendanceTime(AttendanceTime domain) {
		return domain == null ? 0 : domain.valueAsMinutes();
	}

	private static ConstraintTimeDto getConstraintTime(ConstraintTime domain) {
		return domain == null ? null : new ConstraintTimeDto(
						domain.getTotalConstraintTime() == null ? 0
								: domain.getTotalConstraintTime().valueAsMinutes(),
						domain.getLateNightConstraintTime() == null ? 0
								: domain.getLateNightConstraintTime().valueAsMinutes());
	}

	public ActualWorkingTimeOfDaily toDomain() {
		return ActualWorkingTimeOfDaily.of(
					totalWorkingTime == null ? TotalWorkingTime.createAllZEROInstance() : totalWorkingTime.toDomain(), 
					constraintTime == null || constraintTime.getLateNightConstraintTime() == null ? 0 : constraintTime.getLateNightConstraintTime(),
					constraintTime == null || constraintTime.getTotalConstraintTime() == null ? 0 : constraintTime.getTotalConstraintTime(),
					constraintDifferenceTime == null ? 0 : constraintDifferenceTime, 
					timeDifferenceWorkingHours == null ? 0 : timeDifferenceWorkingHours,
				new DivergenceTimeOfDaily(ConvertHelper.mapTo(divergenceTime,
								c -> new DivergenceTime(toAttendanceTimeWithMinus(c.getDivergenceTime()),
										c.getNo(),
										c.getDivergenceReason() == null ? null : new DivergenceReasonContent(c.getDivergenceReason()),
										c.getDivergenceReasonCode() == null ? null : new DiverdenceReasonCode(c.getDivergenceReasonCode())))),
				premiumTime.toDomain());
	}
	
	public void correct(List<EditStateOfDailyPerformanceDto> editStates) {
		
		if (this.totalWorkingTime != null) 
			this.totalWorkingTime.correct(editStates);
	}

	private AttendanceTime toAttendanceTime(Integer value) {
		return value == null ? AttendanceTime.ZERO : new AttendanceTime(value);
	}

	private AttendanceTimeOfExistMinus toAttendanceTimeWithMinus(Integer value) {
		return value == null ? AttendanceTimeOfExistMinus.ZERO : new AttendanceTimeOfExistMinus(value);
	}

	
	@Override
	public Optional<ItemValue> valueOf(String path) {
		switch (path) {
		case (RESTRAINT + DIFF):
			return Optional.of(ItemValue.builder().value(constraintDifferenceTime).valueType(ValueType.TIME));
		case (TIME_DIFF + WORKING_TIME):
			return Optional.of(ItemValue.builder().value(timeDifferenceWorkingHours).valueType(ValueType.TIME));
		default:
			return Optional.empty();
		}
	}
	

	@Override
	public AttendanceItemDataGate newInstanceOf(String path) {
		switch (path) {
		case PREMIUM:
			return new PremiumTimeOfDailyPerformDto();
		case DIVERGENCE:
			return new DivergenceTimeDto();
		case RESTRAINT:
			return new ConstraintTimeDto();
		case TOTAL_LABOR:
			return new TotalWorkingTimeDto();
		default:
			break;
		}
		return null;
	}
	

	@Override
	public Optional<AttendanceItemDataGate> get(String path) {
		switch (path) {
		case PREMIUM:
			return Optional.ofNullable(this.premiumTime);
		case RESTRAINT:
			return Optional.ofNullable(this.constraintTime);
		case TOTAL_LABOR:
			return Optional.ofNullable(this.totalWorkingTime);
		default:
			return Optional.empty();
		}
	}
	

	@Override
	public int size(String path) {
		switch (path) {
		case DIVERGENCE:
			return 10;
		default:
			break;
		}
		return AttendanceItemDataGate.super.size(path);
	}
	

	@Override
	public PropType typeOf(String path) {
		switch (path) {
		case DIVERGENCE:
			return PropType.IDX_LIST;
		case (RESTRAINT + DIFF):
		case (TIME_DIFF + WORKING_TIME):
			return PropType.VALUE;
		default:
		}
		return PropType.OBJECT;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends AttendanceItemDataGate> List<T> gets(String path) {
		switch (path) {
		case DIVERGENCE:
			return (List<T>) this.divergenceTime;
		default:
		}
		return AttendanceItemDataGate.super.gets(path);
	}
	

	@Override
	public void set(String path, ItemValue value) {
		switch (path) {
		case (RESTRAINT + DIFF):
			this.constraintDifferenceTime = value.valueOrDefault(null);
			break;
		case (TIME_DIFF + WORKING_TIME):
			this.timeDifferenceWorkingHours = value.valueOrDefault(null);
			break;
		default:
			break;
		}
	}
	

	@Override
	public void set(String path, AttendanceItemDataGate value) {
		switch (path) {
		case PREMIUM:
			this.premiumTime = (PremiumTimeOfDailyPerformDto) value;
			break;
		case RESTRAINT:
			this.constraintTime = (ConstraintTimeDto) value;
			break;
		case TOTAL_LABOR:
			this.totalWorkingTime = (TotalWorkingTimeDto) value;
			break;
		default:
		}
	}
	

	@Override
	@SuppressWarnings("unchecked")
	public <T extends AttendanceItemDataGate> void set(String path, List<T> value) {
		switch (path) {
		case DIVERGENCE:
			this.divergenceTime = (List<DivergenceTimeDto>) value;
			break;
		default:
		}
	}
}
