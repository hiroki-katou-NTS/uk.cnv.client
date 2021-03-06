package nts.uk.ctx.at.record.app.find.dailyperform.dto;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.app.find.dailyperform.customjson.CustomGeneralDateSerializer;
import nts.uk.ctx.at.record.app.find.dailyperform.editstate.EditStateOfDailyPerformanceDto;
import nts.uk.ctx.at.record.dom.actualworkinghours.AttendanceTimeOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.attendance.util.item.AttendanceItemDataGate;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeOfExistMinus;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.ItemConst;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemRoot;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.AttendanceItemCommon;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ValueType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workschedule.WorkScheduleTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.ActualWorkingTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;

/** 日別実績の勤怠時間 */
@Getter
@Setter
@AttendanceItemRoot(rootName = ItemConst.DAILY_ATTENDANCE_TIME_NAME)
public class AttendanceTimeDailyPerformDto extends AttendanceItemCommon {

	@Override
	public String rootName() { return DAILY_ATTENDANCE_TIME_NAME; }
	/***/
	private static final long serialVersionUID = 1L;
	
	/** 年月日: 年月日 */
	@JsonDeserialize(using = CustomGeneralDateSerializer.class)
	private GeneralDate date;

	/** 社員ID: 社員ID */
	private String employeeID;

	/** 実績時間: 日別実績の勤務実績時間 */
	@AttendanceItemLayout(layout = LAYOUT_A, jpPropertyName = ACTUAL)
	private ActualWorkTimeDailyPerformDto actualWorkTime;

	/** 勤務予定時間: 日別実績の勤務予定時間 */
	@AttendanceItemLayout(layout = LAYOUT_B, jpPropertyName = PLAN)
	private WorkScheduleTimeDailyPerformDto scheduleTime;

	/** 滞在時間: 日別実績の滞在時間 */
	@AttendanceItemLayout(layout = LAYOUT_C, jpPropertyName = STAYING)
	private StayingTimeDto stayingTime;

	/** 医療時間: 日別実績の医療時間 */
	@AttendanceItemLayout(layout = LAYOUT_D, jpPropertyName = MEDICAL, enumField = DEFAULT_ENUM_FIELD_NAME)
	private MedicalTimeDailyPerformDto medicalTime;

	/** 予実差異時間: 勤怠時間 */
	@AttendanceItemLayout(layout = LAYOUT_E, jpPropertyName = PLAN_ACTUAL_DIFF)
	@AttendanceItemValue(type = ValueType.TIME)
	private Integer budgetTimeVariance;

	/** 不就労時間: 勤怠時間 */
	@AttendanceItemLayout(layout = LAYOUT_F, jpPropertyName = UNEMPLOYED)
	@AttendanceItemValue(type = ValueType.TIME)
	private Integer unemployedTime;


	@Override
	public boolean isRoot() { return true; }
	
	@Override
	public Optional<ItemValue> valueOf(String path) {
		switch (path) {
		case PLAN_ACTUAL_DIFF:
			return Optional.of(ItemValue.builder().value(budgetTimeVariance).valueType(ValueType.TIME));
		case UNEMPLOYED:
			return Optional.of(ItemValue.builder().value(unemployedTime).valueType(ValueType.TIME));
		default:
			break;
		}
		return super.valueOf(path);
	}
	
	@Override
	public PropType typeOf(String path) {
		switch (path) {
		case PLAN_ACTUAL_DIFF:
		case UNEMPLOYED:
			return PropType.VALUE;
		default:
			return PropType.OBJECT;
		}
	}

	@Override
	public AttendanceItemDataGate newInstanceOf(String path) {
		switch (path) {
		case ACTUAL:
			return new ActualWorkTimeDailyPerformDto();
		case PLAN:
			return new WorkScheduleTimeDailyPerformDto();
		case STAYING:
			return new StayingTimeDto();
		case MEDICAL:
			return new MedicalTimeDailyPerformDto();
		default:
			break;
		}
		return super.newInstanceOf(path);
	}

	@Override
	public Optional<AttendanceItemDataGate> get(String path) {
		switch (path) {
		case ACTUAL:
			return Optional.ofNullable(actualWorkTime);
		case PLAN:
			return Optional.ofNullable(scheduleTime);
		case STAYING:
			return Optional.ofNullable(stayingTime);
		case MEDICAL:
			return Optional.ofNullable(medicalTime);
		default:
			break;
		}
		return super.get(path);
	}

	@Override
	public void set(String path, ItemValue value) {
		switch (path) {
		case PLAN_ACTUAL_DIFF:
			budgetTimeVariance = value.valueOrDefault(null);
			break;
		case UNEMPLOYED:
			unemployedTime = value.valueOrDefault(null);
			break;
		default:
			break;
		}
	}

	@Override
	public void set(String path, AttendanceItemDataGate value) {
		switch (path) {
		case ACTUAL:
			actualWorkTime = (ActualWorkTimeDailyPerformDto) value;
			break;
		case PLAN:
			scheduleTime = (WorkScheduleTimeDailyPerformDto) value;
			break;
		case STAYING:
			stayingTime = (StayingTimeDto) value;
			break;
		case MEDICAL:
			medicalTime = (MedicalTimeDailyPerformDto) value;
			break;
		default:
			break;
		}
	}
	
	public static AttendanceTimeDailyPerformDto getDto(AttendanceTimeOfDailyPerformance domain) {
		AttendanceTimeDailyPerformDto items = new AttendanceTimeDailyPerformDto();
		if(domain != null){
			items.setEmployeeID(domain.getEmployeeId());
			items.setDate(domain.getYmd());
			items.setActualWorkTime(ActualWorkTimeDailyPerformDto.toActualWorkTime(domain.getTime().getActualWorkingTimeOfDaily()));
			//items.setBudgetTimeVariance(domain.getBudgetTimeVariance().valueAsMinutes());
			items.setBudgetTimeVariance(getAttendanceTime(domain.getTime().getBudgetTimeVariance()));
			items.setMedicalTime(MedicalTimeDailyPerformDto.fromMedicalCareTime(domain.getTime().getMedicalCareTime()));
			items.setScheduleTime(WorkScheduleTimeDailyPerformDto.fromWorkScheduleTime(domain.getTime().getWorkScheduleTimeOfDaily()));
			items.setStayingTime(StayingTimeDto.fromStayingTime(domain.getTime().getStayingTime()));
			items.setUnemployedTime(getAttendanceTime(domain.getTime().getUnEmployedTime()));
			items.exsistData();
		}
		return items;
	}
	
	public static AttendanceTimeDailyPerformDto getDto(String employeeID,GeneralDate ymd,AttendanceTimeOfDailyAttendance domain) {
		AttendanceTimeDailyPerformDto items = new AttendanceTimeDailyPerformDto();
		if(domain != null){
			items.setEmployeeID(employeeID);
			items.setDate(ymd);
			items.setActualWorkTime(ActualWorkTimeDailyPerformDto.toActualWorkTime(domain.getActualWorkingTimeOfDaily()));
			//items.setBudgetTimeVariance(domain.getBudgetTimeVariance().valueAsMinutes());
			items.setBudgetTimeVariance(getAttendanceTime(domain.getBudgetTimeVariance()));
			items.setMedicalTime(MedicalTimeDailyPerformDto.fromMedicalCareTime(domain.getMedicalCareTime()));
			items.setScheduleTime(WorkScheduleTimeDailyPerformDto.fromWorkScheduleTime(domain.getWorkScheduleTimeOfDaily()));
			items.setStayingTime(StayingTimeDto.fromStayingTime(domain.getStayingTime()));
			items.setUnemployedTime(getAttendanceTime(domain.getUnEmployedTime()));
			items.exsistData();
		}
		return items;
	}

	@Override
	public AttendanceTimeDailyPerformDto clone() {
		AttendanceTimeDailyPerformDto items = new AttendanceTimeDailyPerformDto();
		items.setEmployeeID(employeeId());
		items.setDate(workingDate());
		items.setActualWorkTime(actualWorkTime == null ? null : actualWorkTime.clone());
		items.setBudgetTimeVariance(budgetTimeVariance);
		items.setMedicalTime(medicalTime == null ? null : medicalTime.clone());
		items.setScheduleTime(scheduleTime == null ? null : scheduleTime.clone());
		items.setStayingTime(stayingTime == null ? null : stayingTime.clone());
		items.setUnemployedTime(unemployedTime);
		if(isHaveData()){
			items.exsistData();
		}
		return items;
	}
	private static Integer getAttendanceTime(AttendanceTimeOfExistMinus domain) {
		return domain == null ? null : domain.valueAsMinutes();
	}

	@Override
	public String employeeId() {
		return this.employeeID;
	}

	@Override
	public GeneralDate workingDate() {
		return this.date;
	}
	
	@Override
	public AttendanceTimeOfDailyAttendance toDomain(String emp, GeneralDate date) {
		if(!this.isHaveData()) {
			return null;
		}
		if (emp == null) {
			emp = this.employeeId();
		}
		if (date == null) {
			date = this.workingDate();
		}
		AttendanceTimeOfDailyPerformance domain =  new AttendanceTimeOfDailyPerformance(emp, date,
				new AttendanceTimeOfDailyAttendance(
				scheduleTime == null ? WorkScheduleTimeOfDaily.defaultValue() : scheduleTime.toDomain(), 
				actualWorkTime == null ? ActualWorkingTimeOfDaily.defaultValue() : actualWorkTime.toDomain(),
				stayingTime == null ? StayingTimeDto.defaultDomain() : stayingTime.toDomain(), 
				budgetTimeVariance == null ? AttendanceTimeOfExistMinus.ZERO : new AttendanceTimeOfExistMinus(budgetTimeVariance),
				unemployedTime == null ? AttendanceTimeOfExistMinus.ZERO : new AttendanceTimeOfExistMinus(unemployedTime)));
		return domain.getTime();
	}
	
	public void correctWithEditState(List<EditStateOfDailyPerformanceDto> editStates) {

		if (this.actualWorkTime != null) 
			this.actualWorkTime.correct(editStates);
	}
}
