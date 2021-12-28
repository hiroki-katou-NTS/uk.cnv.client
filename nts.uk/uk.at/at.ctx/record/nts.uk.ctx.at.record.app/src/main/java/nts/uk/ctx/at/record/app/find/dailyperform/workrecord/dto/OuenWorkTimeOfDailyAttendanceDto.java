package nts.uk.ctx.at.record.app.find.dailyperform.workrecord.dto;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.attendance.util.item.AttendanceItemDataGate;
import nts.uk.ctx.at.shared.dom.common.amount.AttendanceAmountDaily;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.ItemConst;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ValueType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenAttendanceTimeEachTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SupportFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.WorkingHoursUnitPrice;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenMovementTimeEachTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeOfDailyAttendance;

/** 日別勤怠の応援作業時間 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OuenWorkTimeOfDailyAttendanceDto implements ItemConst, AttendanceItemDataGate {

	/** 応援勤務枠NO: 応援勤務枠NO */
	private int no;
	
	/** 勤務時間: 時間帯別勤怠の時間 */
	@AttendanceItemLayout(layout = LAYOUT_A, jpPropertyName = WORKING_TIME + TIME)
	private OuenAttendanceTimeEachTimeSheetDto workTime;
	
	/** 移動時間: 応援別勤務の移動時間 */
	@AttendanceItemLayout(layout = LAYOUT_B, jpPropertyName = MOVE + TIME)
	private OuenMovementTimeEachTimeSheetDto moveTime;
	
	/** 金額: 勤怠日別金額 */
	@AttendanceItemLayout(layout = LAYOUT_C, jpPropertyName = AMOUNT)
	@AttendanceItemValue(type = ValueType.AMOUNT_NUM)
	private Integer amount;
	
	public OuenWorkTimeOfDailyAttendance toDomain() {
		return OuenWorkTimeOfDailyAttendance.create(
				SupportFrameNo.of(this.no),
				this.workTime == null ? OuenAttendanceTimeEachTimeSheet.createAllZero() : this.workTime.toDomain(),
				this.moveTime == null ? OuenMovementTimeEachTimeSheet.createAllZero() : this.moveTime.toDomain(),
				this.amount == null ? AttendanceAmountDaily.ZERO : new AttendanceAmountDaily(this.amount));
	}
	
	public static OuenWorkTimeOfDailyAttendanceDto toDto(OuenWorkTimeOfDailyAttendance domain) {
		return new OuenWorkTimeOfDailyAttendanceDto(
				domain.getWorkNo().v(),
				OuenAttendanceTimeEachTimeSheetDto.toDto(domain.getWorkTime()),
				OuenMovementTimeEachTimeSheetDto.toDto(domain.getMoveTime()),
				domain.getAmount().v());
	}
	
	@Override
	public PropType typeOf(String path) {
		switch (path) {
		case AMOUNT:
			return PropType.VALUE;
		default:
			return PropType.OBJECT;
		}
	}
	
	@Override
	public AttendanceItemDataGate newInstanceOf(String path) {
		switch (path) {
		case (WORKING_TIME + TIME):
			return new OuenAttendanceTimeEachTimeSheetDto();
		case (MOVE + TIME):
			return new OuenMovementTimeEachTimeSheetDto();
		default:
			return null;
		}
	}
	
	@Override
	public Optional<AttendanceItemDataGate> get(String path) {
		switch (path) {
		case (WORKING_TIME + TIME):
			return Optional.ofNullable(this.workTime);
		case (MOVE + TIME):
			return Optional.ofNullable(this.moveTime);
		default:
			return Optional.empty();
		}
	}
	
	@Override
	public void set(String path, AttendanceItemDataGate value) {
		switch (path) {
		case (WORKING_TIME + TIME):
			this.workTime = (OuenAttendanceTimeEachTimeSheetDto) value;
			break;
		case (MOVE + TIME):
			this.moveTime = (OuenMovementTimeEachTimeSheetDto) value;
			break;
		}
	}
	
	@Override
	public Optional<ItemValue> valueOf(String path) {
		switch (path) {
		case AMOUNT:
			return Optional.of(ItemValue.builder().value(this.amount).valueType(ValueType.AMOUNT_NUM));
		default:
			return Optional.empty();
		}
	}
	
	@Override
	public void set(String path, ItemValue value) {
		switch (path) {
		case AMOUNT:
			this.amount = value.valueOrDefault(0);
			break;
		default:
			break;
		}
	}
}
