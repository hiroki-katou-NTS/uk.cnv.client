package nts.uk.ctx.at.record.app.find.manhourrecordusesetting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecordreferencesetting.ManHourRecordReferenceSetting;

/**
 * 
 * @author huylq
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ManHourRecordReferenceSettingDto {

	private int elapsedMonths;
	
	private int referenceRange;
	
	public static ManHourRecordReferenceSettingDto fromDomain(ManHourRecordReferenceSetting domain) {
		if (domain == null) {
			return null;
		}
		return new ManHourRecordReferenceSettingDto(domain.getElapsedMonths().value, domain.getReferenceRange().value);
	}
}
