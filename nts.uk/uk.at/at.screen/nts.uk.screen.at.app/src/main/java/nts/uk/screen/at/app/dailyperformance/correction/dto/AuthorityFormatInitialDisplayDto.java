package nts.uk.screen.at.app.dailyperformance.correction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityFormatInitialDisplayDto {
	
	private String companyId;

	private String dailyPerformanceFormatCode;
	
	private Integer pcSpAtr;
}
