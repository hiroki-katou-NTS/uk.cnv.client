package nts.uk.ctx.at.record.app.find.monthly.root.dto;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.attendance.util.item.AttendanceItemDataGate;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.ItemConst;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ValueType;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceAmountMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workamount.WorkAmountOfMonthly;

@Data
@NoArgsConstructor
@AllArgsConstructor
/** 月別実績の勤務金額 */
public class WorkAmountOfMonthlyDto implements ItemConst, AttendanceItemDataGate {
	
	/** 就業時間金額 */
	private long workTimeAmount;
	
	public static WorkAmountOfMonthlyDto from(WorkAmountOfMonthly domain) {
		WorkAmountOfMonthlyDto dto = new WorkAmountOfMonthlyDto();
		if(domain != null) {
			dto.setWorkTimeAmount(domain.getWorkTimeAmount().v());
		}
		return dto;
	}

	public WorkAmountOfMonthly toDomain() {
		return WorkAmountOfMonthly.of(new AttendanceAmountMonth(this.workTimeAmount));
	}

	@Override
	public Optional<ItemValue> valueOf(String path) {
		switch (path) {
		case WORK_TIME:
			return Optional.of(ItemValue.builder().value(workTimeAmount).valueType(ValueType.AMOUNT_NUM));
		default:
			break;
		}
		return AttendanceItemDataGate.super.valueOf(path);
	}

	@Override
	public PropType typeOf(String path) {
		switch (path) {
		case WORK_TIME:
			return PropType.VALUE;
		default:
			break;
		}
		return AttendanceItemDataGate.super.typeOf(path);
	}

	@Override
	public void set(String path, ItemValue value) {
		switch (path) {
		case WORK_TIME:
			workTimeAmount = value.valueOrDefault(0); break;
		default:
			break;
		}
	}
}
