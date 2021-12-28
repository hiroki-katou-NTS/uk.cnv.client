package nts.uk.screen.at.app.query.kdw.kdw003.g;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class EmployeeInfoDto {
	private String id;
	private String code;
	private String businessName;
	private String workplaceName;
	private String workplaceId;
	private String depName;
}
