package nts.uk.screen.at.app.dailyperformance.correction.dto.mobile;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MasterDialogParam {
	
	private List<Integer> types; 
	
	private GeneralDate date;
	
	private String employeeID;
	
	private String workTypeCD;
	
	private List<Integer> itemIds;
}
