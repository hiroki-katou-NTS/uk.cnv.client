package nts.uk.ctx.at.record.app.find.dailyattendance.timesheet.ouen.dto;

import java.util.Optional;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.attendance.util.item.AttendanceItemDataGate;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.ItemConst;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.ReasonTimeChange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkTimeInformation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ValueType;
import nts.uk.shr.com.time.TimeWithDayAttr;

@Data
@NoArgsConstructor
// 勤務時刻情報
public class WorkTimeInformationDto implements ItemConst, AttendanceItemDataGate {

	@Setter
	// 時刻変更理由
	private ReasonTimeChangeDto reasonTimeChange;

	@Setter
	// 時刻
	@AttendanceItemLayout(layout = LAYOUT_U, jpPropertyName = CLOCK)
	@AttendanceItemValue(type = ValueType.CLOCK)
	private Integer timeWithDay;

	@Override
	protected WorkTimeInformationDto clone() {
		return new WorkTimeInformationDto(reasonTimeChange, timeWithDay);
	}

	@Override
	public Optional<ItemValue> valueOf(String path) {
		switch (path) {
		case CLOCK:
			return Optional.of(ItemValue.builder().value(timeWithDay).valueType(ValueType.CLOCK));
		default:
			return Optional.empty();
		}
	}

	@Override
	public void set(String path, ItemValue value) {
		switch (path) {
		case CLOCK:
			this.timeWithDay = value.valueOrDefault(0);
			break;
		default:
			break;
		}
	}

	@Override
	public PropType typeOf(String path) {
		switch (path) {
		case CLOCK:
			return PropType.VALUE;
		default:
			break;
		}
		return AttendanceItemDataGate.super.typeOf(path);
	}

	public WorkTimeInformationDto(ReasonTimeChangeDto reasonTimeChange, Integer timeWithDay) {
		super();
		this.reasonTimeChange = reasonTimeChange;
		this.timeWithDay = timeWithDay;
	}

	public static WorkTimeInformationDto fromDomain(WorkTimeInformation domain) {

		return new WorkTimeInformationDto(ReasonTimeChangeDto.fromDomain(domain.getReasonTimeChange()), domain.getTimeWithDay().map(x-> x.v()).orElse(null));
	}
	
	public WorkTimeInformation domain() {
		
		return new WorkTimeInformation(
				reasonTimeChange == null ? ReasonTimeChange.createByAutomaticSet() : reasonTimeChange.domain(), 
				timeWithDay == null ? null : new TimeWithDayAttr(timeWithDay));
	}
}
