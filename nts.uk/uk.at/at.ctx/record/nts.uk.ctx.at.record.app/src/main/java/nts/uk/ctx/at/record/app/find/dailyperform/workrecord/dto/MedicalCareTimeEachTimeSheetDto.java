package nts.uk.ctx.at.record.app.find.dailyperform.workrecord.dto;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.app.find.dailyperform.dto.PremiumTimeDto;
import nts.uk.ctx.at.shared.dom.attendance.util.item.AttendanceItemDataGate;
import nts.uk.ctx.at.shared.dom.attendance.util.item.AttendanceItemDataGate.PropType;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.ItemConst;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ValueType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.MedicalCareTimeEachTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.MedicalCareTimeEachTimeSheet.FullTimeNightShiftAttr;

/** 時間帯別勤怠の医療時間 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalCareTimeEachTimeSheetDto implements ItemConst, AttendanceItemDataGate {

	/** 区分：常勤夜勤区分 */
	@AttendanceItemLayout(layout = LAYOUT_A, jpPropertyName = DAY_SHIFT + NIGHT_SHIFT + ATTRIBUTE)
	@AttendanceItemValue(type = ValueType.ATTR)
	private int attr;
	
	/** 勤務時間：勤怠時間 */
	@AttendanceItemLayout(layout = LAYOUT_B, jpPropertyName = WORKING_TIME + TIME)
	@AttendanceItemValue(type = ValueType.TIME)
	private Integer workTime;
	
	/** 控除時間：勤怠時間 */
	@AttendanceItemLayout(layout = LAYOUT_C, jpPropertyName = DEDUCTION + TIME)
	@AttendanceItemValue(type = ValueType.TIME)
	private Integer deductionTime;
	
	/** 休憩時間：勤怠時間 */
	@AttendanceItemLayout(layout = LAYOUT_D, jpPropertyName = BREAK + TIME)
	@AttendanceItemValue(type = ValueType.TIME)
	private Integer breakTime;
	
	public MedicalCareTimeEachTimeSheet toDomain() {
		return MedicalCareTimeEachTimeSheet.create(
				FullTimeNightShiftAttr.of(attr),
				this.workTime == null ? AttendanceTime.ZERO : new AttendanceTime(this.workTime),
				this.breakTime == null ? AttendanceTime.ZERO : new AttendanceTime(this.breakTime),
				this.deductionTime == null ? AttendanceTime.ZERO : new AttendanceTime(this.deductionTime));
	}
	
	public static MedicalCareTimeEachTimeSheetDto toDto(MedicalCareTimeEachTimeSheet domain) {
		return new MedicalCareTimeEachTimeSheetDto(
				domain.getAttr().value,
				domain.getWorkTime().valueAsMinutes(),
				domain.getDeductionTime().valueAsMinutes(),
				domain.getBreakTime().valueAsMinutes());
	}
	
	public String enumText() {
		switch (this.attr) {
		case 0:
			return DAY_SHIFT;
		case 1:
			return NIGHT_SHIFT;
		default:
			return EMPTY_STRING;
		}
	}
	
	@Override
	public void setEnum(String enumText) {
		switch (enumText) {
		case DAY_SHIFT:
			this.attr = 0;
			break;
		case NIGHT_SHIFT:
			this.attr = 1;
			break;
		default:
		}
	}
	
	@Override
	public PropType typeOf(String path) {
		switch (path) {
		case (WORKING_TIME + TIME):
		case (DEDUCTION + TIME):
		case (BREAK + TIME):
			return PropType.VALUE;
		default:
			return PropType.OBJECT;
		}
	}
	
	@Override
	public Optional<ItemValue> valueOf(String path) {
		switch (path) {
		case (WORKING_TIME + TIME):
			return Optional.of(ItemValue.builder().value(this.workTime).valueType(ValueType.TIME));
		case (DEDUCTION + TIME):
			return Optional.of(ItemValue.builder().value(this.deductionTime).valueType(ValueType.TIME));
		case (BREAK + TIME):
			return Optional.of(ItemValue.builder().value(this.breakTime).valueType(ValueType.TIME));
		default:
			return Optional.empty();
		}
	}
	
	@Override
	public void set(String path, ItemValue value) {
		switch (path) {
		case (WORKING_TIME + TIME):
			this.workTime = value.valueOrDefault(0);
			break;
		case (DEDUCTION + TIME):
			this.deductionTime = value.valueOrDefault(0);
			break;
		case (BREAK + TIME):
			this.breakTime = value.valueOrDefault(0);
			break;
		default:
			break;
		}
	}
}
