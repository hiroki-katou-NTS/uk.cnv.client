package nts.uk.screen.at.app.command.kdw.kdw003.g;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HistCommandUpdate {
	private String employeeId;
	private String startDate;
	private String endDate;

	private String oldStartDate;
//	private String oldEndDate;
	private List<String> lstTask;

	public DatePeriod toDatePeriod() {
		GeneralDate startDate = GeneralDate.fromString(this.startDate, "yyyy/MM/dd");
		GeneralDate endDate = GeneralDate.fromString(this.endDate, "yyyy/MM/dd");
		return new DatePeriod(startDate, endDate);
	}

	public GeneralDate toCurrentStartDate() {
		return GeneralDate.fromString(this.oldStartDate, "yyyy/MM/dd");
	}
//	public DatePeriod toOldDatePeriod() {
//		GeneralDate startDate = GeneralDate.fromString(this.oldStartDate, "yyyy/MM/dd");
//		GeneralDate endDate = GeneralDate.fromString(this.oldEndDate, "yyyy/MM/dd");
//		return new DatePeriod(startDate, endDate);
//	}
}
