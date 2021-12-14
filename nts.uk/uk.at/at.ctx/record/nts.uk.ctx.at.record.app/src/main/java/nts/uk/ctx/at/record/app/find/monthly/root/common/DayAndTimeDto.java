package nts.uk.ctx.at.record.app.find.monthly.root.common;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.attendance.util.item.AttendanceItemDataGate;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.DayOffDayAndTimes;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.DayOffDayTimeUnUse;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.DayOffDayTimeUse;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.DayOffRemainCarryForward;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.DayOffRemainDayAndTimes;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.MonthVacationGrantDay;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.MonthVacationGrantTime;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.ItemConst;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ValueType;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.specialholiday.ActualSpecialLeaveRemain;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.specialholiday.ActualSpecialLeaveRemainDay;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.specialholiday.SpecialLeavaRemainTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.specialholiday.SpecialLeaveRemain;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.specialholiday.SpecialLeaveRemainDay;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.specialholiday.SpecialLeaveUnDigestion;

@NoArgsConstructor
@AllArgsConstructor
public class DayAndTimeDto implements ItemConst, AttendanceItemDataGate {
	
	/** 日数 */
	@AttendanceItemValue(type = ValueType.DAYS)
	@AttendanceItemLayout(jpPropertyName = DAYS, layout = LAYOUT_A)
	private Double days;
	
	/** 時間 */
	@AttendanceItemValue(type = ValueType.TIME)
	@AttendanceItemLayout(jpPropertyName = TIME, layout = LAYOUT_B)
	private Integer time;
	
	public static DayAndTimeDto from(ActualSpecialLeaveRemain domain){
		return domain == null ? null : new DayAndTimeDto(domain.getDays().v(), domain.getTime().isPresent() ? domain.getTime().get().valueAsMinutes() : null);
	}
	public static DayAndTimeDto from(SpecialLeaveRemain domain){
		return domain == null ? null : new DayAndTimeDto(domain.getDays().v(), domain.getTime().isPresent() ? domain.getTime().get().valueAsMinutes() : null);
	}
	public static DayAndTimeDto from(SpecialLeaveUnDigestion domain){
		return domain == null ? null : new DayAndTimeDto(domain.getDays().v(), domain.getTimes().isPresent() ? domain.getTimes().get().valueAsMinutes() : null);
	}
	
	public SpecialLeaveRemain toSpecial(){
		return new SpecialLeaveRemain(new SpecialLeaveRemainDay(days == null ? 0 : days),
				Optional.ofNullable(time == null ? null : new SpecialLeavaRemainTime(time)));
	}
	public ActualSpecialLeaveRemain toActualSpecial(){
		return new ActualSpecialLeaveRemain(new ActualSpecialLeaveRemainDay(days == null ? 0 : days),
				Optional.ofNullable(time == null ? null : new SpecialLeavaRemainTime(time)));
	}
	public SpecialLeaveUnDigestion toUnDegest(){
		return new SpecialLeaveUnDigestion(new SpecialLeaveRemainDay(days == null ? 0 : days),
				Optional.ofNullable(time == null ? null : new SpecialLeavaRemainTime(time)));
	}
	
	public static DayAndTimeDto from(DayOffDayAndTimes domain){
		return domain == null ? null : new DayAndTimeDto(domain.getDay().v(), domain.getTime().isPresent() ? domain.getTime().get().valueAsMinutes() : null);
	}
	
	public DayOffRemainCarryForward toCarry(){
		return new DayOffRemainCarryForward(new LeaveRemainingDayNumber(days == null ? 0 : days),
				Optional.ofNullable(time == null ? null : new LeaveRemainingTime(time)));
	}
	
	public static DayAndTimeDto from(DayOffRemainCarryForward domain){
		return domain == null ? null : new DayAndTimeDto(domain.getDay().v(), domain.getTime().isPresent() ? domain.getTime().get().valueAsMinutes() : null);
	}
	
	public DayOffDayAndTimes toOccr() {
		return new DayOffDayAndTimes(new MonthVacationGrantDay(days == null ? 0 : days),
				Optional.ofNullable(time == null ? null : new MonthVacationGrantTime(time)));
	}
	
	public DayOffDayTimeUse toUse() {
		return new DayOffDayTimeUse(new LeaveUsedDayNumber(days == null ? 0 : days),
				Optional.ofNullable(time == null ? null : new LeaveUsedTime(time)));
	}
	
	public static DayAndTimeDto from(DayOffDayTimeUse domain){
		return domain == null ? null : new DayAndTimeDto(domain.getDay().v(), domain.getTime().isPresent() ? domain.getTime().get().valueAsMinutes() : null);
	}
	
	public DayOffRemainDayAndTimes toRemain() {
		return new DayOffRemainDayAndTimes(new LeaveRemainingDayNumber(days == null ? 0 : days),
				Optional.ofNullable(time == null ? null : new LeaveRemainingTime(time)));
	}
	
	public DayOffDayTimeUnUse toUnUse() {
		return new DayOffDayTimeUnUse(new LeaveRemainingDayNumber(days == null ? 0 : days),
				Optional.ofNullable(time == null ? null : new LeaveRemainingTime(time)));
	}
	
	public static DayAndTimeDto from(DayOffDayTimeUnUse domain){
		return domain == null ? null : new DayAndTimeDto(domain.getDay().v(), domain.getTime().isPresent() ? domain.getTime().get().valueAsMinutes() : null);
	}
	
	public static DayAndTimeDto from(DayOffRemainDayAndTimes domain){
		return domain == null ? null : new DayAndTimeDto(domain.getDay().v(), domain.getTime().isPresent() ? domain.getTime().get().valueAsMinutes() : null);
	}
	
	@Override
	public Optional<ItemValue> valueOf(String path) {
		switch (path) {
		case DAYS:
			return Optional.of(ItemValue.builder().value(days).valueType(ValueType.DAYS));
		case TIME:
			return Optional.of(ItemValue.builder().value(time).valueType(ValueType.TIME));
		default:
			break;
		}
		return AttendanceItemDataGate.super.valueOf(path);
	}
	@Override
	public PropType typeOf(String path) {
		switch (path) {
		case DAYS:
		case TIME:
			return PropType.VALUE;
		default:
			break;
		}
		return AttendanceItemDataGate.super.typeOf(path);
	}
	@Override
	public void set(String path, ItemValue value) {
		switch (path) {
		case DAYS:
			days = value.valueOrDefault(null); break;
		case TIME:
			time = value.valueOrDefault(null); break;
		default:
			break;
		}
	}

	
}
