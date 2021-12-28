package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainObject;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;

@Getter
/** 時間帯別勤怠の医療時間 */
public class MedicalCareTimeEachTimeSheet implements DomainObject {

	/** 区分: 常勤夜勤区分 */
	private FullTimeNightShiftAttr attr;
	
	/** 勤務時間: 勤怠時間 */
	private AttendanceTime workTime;
	
	/** 休憩時間: 勤怠時間 */
	private AttendanceTime breakTime;
	
	/** 控除時間: 勤怠時間 */
	private AttendanceTime deductionTime;
	
	private MedicalCareTimeEachTimeSheet(FullTimeNightShiftAttr attr, 
			AttendanceTime workTime, AttendanceTime breakTime,
			AttendanceTime deductionTime) {
		super();
		this.attr = attr;
		this.workTime = workTime;
		this.breakTime = breakTime;
		this.deductionTime = deductionTime;
	}
	
	public static MedicalCareTimeEachTimeSheet create(FullTimeNightShiftAttr attr, 
			AttendanceTime workTime, AttendanceTime breakTime, AttendanceTime deductionTime){
		
		return new MedicalCareTimeEachTimeSheet(attr, workTime, breakTime, deductionTime);
	}
	
	@AllArgsConstructor
	public static enum FullTimeNightShiftAttr {
		
		/** 常勤 */
		DAY_SHIFT(0),

		/** 夜勤 */
		NIGHT_SHIFT(1);
		
		public int value;
		
		public static FullTimeNightShiftAttr of(int value) {
			switch(value) {
			case 0:
				return DAY_SHIFT;
			case 1:
				return NIGHT_SHIFT;
			default:
				return DAY_SHIFT;
			}
		}
	}
	
	/**
	 * 全て0で作成する
	 * @return 時間帯別勤怠の医療時間(List)
	 */
	public static List<MedicalCareTimeEachTimeSheet> createAllZero() {
		List<MedicalCareTimeEachTimeSheet> medicalCareTime = new ArrayList<MedicalCareTimeEachTimeSheet>();
		medicalCareTime.add(new MedicalCareTimeEachTimeSheet(
				FullTimeNightShiftAttr.DAY_SHIFT, AttendanceTime.ZERO, AttendanceTime.ZERO, AttendanceTime.ZERO));
		medicalCareTime.add(new MedicalCareTimeEachTimeSheet(
				FullTimeNightShiftAttr.NIGHT_SHIFT, AttendanceTime.ZERO, AttendanceTime.ZERO, AttendanceTime.ZERO));
		return medicalCareTime;
	}
}
