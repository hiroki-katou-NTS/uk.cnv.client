package nts.uk.screen.at.app.kml002.H;
/**
 * 
 * @author quytb
 *
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.HandlingOfCriterionAmountByNo;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HandlingOfCriterionAmountByNoDto {
	private Integer frameNo;
	private String backgroundColor;
	
	public static HandlingOfCriterionAmountByNoDto setData(HandlingOfCriterionAmountByNo data) {
		return new HandlingOfCriterionAmountByNoDto(data.getFrameNo().v(), data.getBackgroundColor().v());
	}
}
