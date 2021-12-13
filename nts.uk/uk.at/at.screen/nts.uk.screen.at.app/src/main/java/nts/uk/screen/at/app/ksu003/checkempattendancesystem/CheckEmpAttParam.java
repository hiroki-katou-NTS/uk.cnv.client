package nts.uk.screen.at.app.ksu003.checkempattendancesystem;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.calendar.period.DatePeriod;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CheckEmpAttParam {
	public List<String> lstEmpId;
	public String startDate;
	public String endDate;
	public int displayMode;
}
