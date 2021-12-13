package nts.uk.screen.at.app.ksu001.start;

import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WorkMapDtoList {

	public String sid;
	
	public List<WorkMapDto> workHours = Collections.emptyList();
}
