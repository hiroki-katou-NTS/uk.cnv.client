package nts.uk.screen.at.app.ksu001.start;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EstimatedSalaryMapDto {
	
	public String sid;
	
	/** 給与額 **/
	public BigDecimal salary;

	/** 目安金額 **/
	public Integer criterion;

	/** 目安金額背景色 **/
	public String background;
	
}
