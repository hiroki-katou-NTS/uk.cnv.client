package nts.uk.ctx.at.record.app.find.dailyperform.dto;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.attendance.util.item.AttendanceItemDataGate;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.ItemConst;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ValueType;

/** 休憩時間帯 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BreakTimeSheetDto implements ItemConst, AttendanceItemDataGate {

	/** 開始: 勤怠打刻 */
	@AttendanceItemLayout(layout = LAYOUT_A, jpPropertyName = START)
	@AttendanceItemValue(type = ValueType.TIME_WITH_DAY)
	private Integer start;

	/** 終了: 勤怠打刻 */
	@AttendanceItemLayout(layout = LAYOUT_B, jpPropertyName = END)
	@AttendanceItemValue(type = ValueType.TIME_WITH_DAY)
	private Integer end;

	/** 休憩時間: 勤怠打刻 */
//	@AttendanceItemLayout(layout = "C")
//	@AttendanceItemValue(itemId = -1, type = ValueType.INTEGER)
	private Integer breakTime;

	/** 休憩枠NO: 休憩枠NO */
//	@AttendanceItemLayout(layout = "D")
//	@AttendanceItemValue(itemId = -1, type = ValueType.INTEGER)
	private int no;
	
	@Override
	public Optional<ItemValue> valueOf(String path) {
		switch (path) {
		case START:
			return Optional.of(ItemValue.builder().value(start).valueType(ValueType.TIME_WITH_DAY));
		case (END):
			return Optional.of(ItemValue.builder().value(end).valueType(ValueType.TIME_WITH_DAY));
		default:
			return Optional.empty();
		}
	}
	
	@Override
	public PropType typeOf(String path) {
		switch (path) {
		case START:
		case END:
			return PropType.VALUE;
		default:
			return PropType.OBJECT;
		}
	}

	@Override
	public void set(String path, ItemValue value) {
		switch (path) {
		case START:
			this.start = value.valueOrDefault(null);
			break;
		case (END):
			this.end = value.valueOrDefault(null);
			break;
		default:
			break;
		}
	}
	
	@Override
	public BreakTimeSheetDto clone() {
		return new BreakTimeSheetDto(start, end, breakTime, no);
	}

	public static BreakTimeSheetDto fromDomain(BreakTimeSheet bt) {

		return new BreakTimeSheetDto(
				bt.getStartTime().v(),
				bt.getEndTime().v(),
				bt.getBreakTime().v(),
				bt.getBreakFrameNo().v());
	}
}
