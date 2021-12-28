package nts.uk.ctx.at.function.app.command.dailyperformanceformat;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DuplicateAuthorityDailyFormatCommand {
	
	private String dailyPerformanceFormatCode;
	
	private String dailyPerformanceFormatName;
	
	private List<DailyFormSheetCommand> listDailyFormSheetCommand;
	
	private AddAuthorityMonthlyCommand authorityMonthlyCommand;
	
}
