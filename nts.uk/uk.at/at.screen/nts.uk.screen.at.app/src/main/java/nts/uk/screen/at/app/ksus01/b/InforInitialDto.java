package nts.uk.screen.at.app.ksus01.b;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InforInitialDto {
	
	//	総労働時間
	private BigDecimal totalWorkingTime;
	//	月間想定給与額
	private BigDecimal estimatedSalaryMonthly;
	//	累計想定給与額
	private BigDecimal estimatedSalaryCumulative;
	
}
