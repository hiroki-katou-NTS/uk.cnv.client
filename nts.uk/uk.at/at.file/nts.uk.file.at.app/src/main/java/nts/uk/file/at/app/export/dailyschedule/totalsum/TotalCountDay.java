package nts.uk.file.at.app.export.dailyschedule.totalsum;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 日数計項目
 * @author HoangNDH
 *
 */
@Data
public class TotalCountDay {
	// 出勤日数
	private int workingDay;
	// 休日日数
	private int holidayDay;
	// 休出日数
	private int offDay;
	// 年休使用数
	private int yearOffUsage;
	// 積休使用数
	private int heavyHolDay;
	// 特休日数
	private int specialHoliday;
	// 欠勤日数
	private int absenceDay;
	// 遅刻回数
	private int lateComeDay;
	// 早退回数
	private int earlyLeaveDay;
	// 所定日数
	private int predeterminedDay;	
	
	/** The all day count. */
	public List<String> allDayCount = new ArrayList<>();
	
	/**
	 * Inits all day count.
	 */
	public void initAllDayCount() {
		allDayCount.add(predeterminedDay + "日");	// 所定日数
		allDayCount.add(holidayDay + "日");			// 休日日数
		allDayCount.add(offDay + "日");				// 休出日数
		allDayCount.add(yearOffUsage + "日");		// 年休使用数
		allDayCount.add(heavyHolDay + "日");			// 積休使用数
		allDayCount.add(specialHoliday + "日");		// 特休日数
		allDayCount.add(absenceDay + "日");			// 欠勤日数
		allDayCount.add(lateComeDay + "回");			// 遅刻回数
		allDayCount.add(earlyLeaveDay + "回");		// 早退回数
	}
}
