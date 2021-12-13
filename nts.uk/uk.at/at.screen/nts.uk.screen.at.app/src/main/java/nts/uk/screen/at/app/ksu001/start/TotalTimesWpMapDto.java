package nts.uk.screen.at.app.ksu001.start;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TotalTimesWpMapDto {

	
	
	/** 回数集計NO */
	public Integer totalCountNo;
	
	/** 回数集計名称 */
	public String totalTimesName;
	
	public BigDecimal value;
}
