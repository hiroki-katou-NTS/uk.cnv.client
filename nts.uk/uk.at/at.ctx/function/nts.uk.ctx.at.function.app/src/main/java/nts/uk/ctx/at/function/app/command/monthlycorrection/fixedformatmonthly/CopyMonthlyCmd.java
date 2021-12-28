package nts.uk.ctx.at.function.app.command.monthlycorrection.fixedformatmonthly;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CopyMonthlyCmd {

	private String businessTypeCode;
	
	private List<String> listBusinessTypeCode;
}
