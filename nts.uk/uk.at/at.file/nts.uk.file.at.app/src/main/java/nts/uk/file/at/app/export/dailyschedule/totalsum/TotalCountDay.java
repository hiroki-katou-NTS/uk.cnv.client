package nts.uk.file.at.app.export.dailyschedule.totalsum;

import java.text.DecimalFormat;
import java.text.NumberFormat;
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
	private double workingDay;
	// 休日日数
	private double holidayDay;
	// 休出日数
	private double offDay;
	// 年休使用数
	private double yearOffUsage;
	// 積休使用数
	private double heavyHolDay;
	// 特休日数
	private double specialHoliday;
	// 欠勤日数
	private double absenceDay;
	// 遅刻回数
	private int lateComeDay;
	// 早退回数
	private int earlyLeaveDay;
	// 所定日数
	private double predeterminedDay;	
	
	/** The all day count. */
	public List<String> allDayCount = new ArrayList<>();
	
	/**
	 * Inits all day count.
	 */
	public void initAllDayCount() {
		NumberFormat formatter = new DecimalFormat("#0.0"); 
		allDayCount.add(formatter.format(predeterminedDay) + "日");		// 所定日数
		allDayCount.add(formatter.format(workingDay) + "日");			// 出勤日数
		allDayCount.add(formatter.format(holidayDay) + "日");			// 休日日数
		allDayCount.add(formatter.format(offDay) + "日");				// 休出日数
		allDayCount.add(formatter.format(yearOffUsage) + "日");			// 年休使用数
		allDayCount.add(formatter.format(heavyHolDay) + "日");			// 積休使用数
		allDayCount.add(formatter.format(specialHoliday) + "日");		// 特休日数
		allDayCount.add(formatter.format(absenceDay) + "日");			// 欠勤日数
		allDayCount.add(lateComeDay + "回");								// 遅刻回数
		allDayCount.add(earlyLeaveDay + "回");							// 早退回数
	}
}
