package nts.uk.screen.at.app.ksus01.b;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InforInitialInput {
	
//	private List<WorkScheduleDto> listWorkSchedule;
	
	private String startDate;
	
	private String endDate;
	
	private int baseYM;
	
	private int closingDay;
}
