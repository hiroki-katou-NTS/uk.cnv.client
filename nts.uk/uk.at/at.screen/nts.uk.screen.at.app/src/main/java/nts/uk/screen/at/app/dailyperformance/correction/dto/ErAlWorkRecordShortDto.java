package nts.uk.screen.at.app.dailyperformance.correction.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErAlWorkRecordShortDto {
	
	private String date;
	
	private String employeeID;
	
	private String code;
	
	private String name;
	
	private String type;
	
	private List<Integer> attendanceItemList;
	
}
