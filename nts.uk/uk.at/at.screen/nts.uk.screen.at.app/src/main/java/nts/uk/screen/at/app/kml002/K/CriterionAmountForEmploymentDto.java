package nts.uk.screen.at.app.kml002.K;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.screen.at.app.kml002.H.CriterionAmountByNoDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CriterionAmountForEmploymentDto {
	
	private String employmentCode;
	
	private List<CriterionAmountByNoDto> months;
	
	private List<CriterionAmountByNoDto> years;
}
