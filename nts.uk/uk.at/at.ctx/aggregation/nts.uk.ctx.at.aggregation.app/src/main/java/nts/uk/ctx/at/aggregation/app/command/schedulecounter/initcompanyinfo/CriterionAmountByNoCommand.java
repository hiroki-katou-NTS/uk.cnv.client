package nts.uk.ctx.at.aggregation.app.command.schedulecounter.initcompanyinfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountByNo;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountNo;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountValue;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CriterionAmountByNoCommand {
	
	private Integer frameNo;
	
	private Integer amount;
	
	public CriterionAmountByNo toCriterionAmountByNo() {

		return new CriterionAmountByNo(new CriterionAmountNo(frameNo), new CriterionAmountValue(amount));
	}
}
