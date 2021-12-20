package nts.uk.screen.at.app.query.ksu.ksu002.a.input;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.DayOfWeek;
import nts.arc.time.calendar.period.DatePeriod;

/**
 * 
 * @author chungnt
 *
 */

@AllArgsConstructor
@NoArgsConstructor
public class DisplayInWorkInfoInput {
	public List<String> listSid;
	public String startDate;
	public String endDate;
	public boolean actualData;
	
	public Integer startWeekDate;
	
	public GeneralDate getStartDate() {
		return GeneralDate.fromString(startDate, "yyyy/MM/dd");
	}
	
	public GeneralDate getEndDate() {
		return GeneralDate.fromString(endDate, "yyyy/MM/dd");
	}
	
	public DatePeriod getPeriod() {
		return new DatePeriod(getStartDate(), getEndDate());
	}
	
	public DayOfWeek getStartWeekDate() {
		return DayOfWeek.valueOf(startWeekDate);
	}
}
