package nts.uk.screen.at.app.ksu001.aggrerateworkplacetotal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.bs.employee.dom.classification.Classification;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClassificationDto {

	public String companyId;
	
	/** The classification code. */
	public String classificationCode;
	
	/** The classification name. */
	public String classificationName;
	
	/** The classification memo. */
	public  String memo;
	
	public static ClassificationDto fromDomain(Classification domain) {
		
		return new ClassificationDto(
				domain.getCompanyId().v(),
				domain.getClassificationCode().v(),
				domain.getClassificationName().v(),
				domain.getMemo().v()
				);
	}
}
