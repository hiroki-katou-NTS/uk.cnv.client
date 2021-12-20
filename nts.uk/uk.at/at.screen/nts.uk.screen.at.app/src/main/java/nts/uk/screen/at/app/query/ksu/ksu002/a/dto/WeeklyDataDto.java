package nts.uk.screen.at.app.query.ksu.ksu002.a.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WeeklyDataDto {

	//・No for order
	public int no;
	
	//労働時間
	public Double workingHoursMonth = 0.0;
	
	//休日日数
	public Double numberHolidaysCurrentMonth = 0.0;
}
