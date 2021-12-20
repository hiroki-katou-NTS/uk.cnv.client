package nts.uk.ctx.at.aggregation.app.command.schedulecounter.initcompanyinfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountNo;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.HandlingOfCriterionAmountByNo;
import nts.uk.shr.com.color.ColorCode;
/**
 * 
 * @author quytb
 *
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HandlingOfCriterionAmountByNoComand {
	private Integer frameNo;

	private String backgroundColor;

	public HandlingOfCriterionAmountByNo toHandlingOfCriterionAmountByNo() {
		return new HandlingOfCriterionAmountByNo(new CriterionAmountNo(frameNo), new ColorCode(backgroundColor.substring(1)));
	}

}
