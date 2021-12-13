package nts.uk.screen.at.app.ksu001.start;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class TotalTimesMapDtoList {
	
	public String sid;
	
	public List<TotalTimesMapDto> totalTimesMapDto;
}
