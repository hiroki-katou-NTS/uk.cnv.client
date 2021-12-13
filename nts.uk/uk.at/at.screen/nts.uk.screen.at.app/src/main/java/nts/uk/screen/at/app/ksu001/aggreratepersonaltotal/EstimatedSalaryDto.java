package nts.uk.screen.at.app.ksu001.aggreratepersonaltotal;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.personcounter.EstimatedSalary;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EstimatedSalaryDto {
	/** 給与額 **/
	public BigDecimal salary;

	/** 目安金額 **/
	public Integer criterion;

	/** 目安金額背景色 **/
	public String background;
	
	public static EstimatedSalaryDto fromDomain(EstimatedSalary domain) {
		
		return new EstimatedSalaryDto(
				domain.getSalary(),
				domain.getCriterion().v(),
				domain.getBackground().map(x -> x.v()).orElse("")
				);
	}
}
