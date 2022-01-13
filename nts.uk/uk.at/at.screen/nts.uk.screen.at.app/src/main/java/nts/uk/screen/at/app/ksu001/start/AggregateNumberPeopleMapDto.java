package nts.uk.screen.at.app.ksu001.start;

import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor	
@NoArgsConstructor
@Data
public class AggregateNumberPeopleMapDto {
	
	public List<NumberPeopleMapDtoList> employment = Collections.emptyList();
	
	public List<NumberPeopleMapDtoList> classification = Collections.emptyList();
	
	public List<NumberPeopleMapDtoList> jobTitleInfo = Collections.emptyList();
}
