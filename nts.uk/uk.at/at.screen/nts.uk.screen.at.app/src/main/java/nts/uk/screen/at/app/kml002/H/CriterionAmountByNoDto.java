package nts.uk.screen.at.app.kml002.H;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountByNo;
/**
 * 
 * @author hoangnd
 *
 */
// 要件別目安金額
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CriterionAmountByNoDto {
	
	private Integer frameNo;
	
	private Integer amount;
	
	
	public static CriterionAmountByNoDto setData(CriterionAmountByNo data) {
		
		return new CriterionAmountByNoDto(data.getFrameNo().v(), data.getAmount().v());
	}
}
