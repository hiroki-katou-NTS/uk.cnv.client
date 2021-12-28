package nts.uk.ctx.at.function.app.command.dailyperformanceformat;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.function.dom.dailyperformanceformat.AuthorityFomatDaily;
import nts.uk.ctx.at.function.dom.dailyperformanceformat.AuthorityFormatSheet;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DailyFormSheetCommand {

	private BigDecimal sheetNo;
	
	private String sheetName;
	
	private List<DailyFormItemCommand> listDailyFormItemCommand;
	
	public static DailyFormSheetCommand toDto(AuthorityFormatSheet authorityFormatSheet, List<AuthorityFomatDaily> listAuthorityFomatDaily) {
		List<DailyFormItemCommand> listDailyFormItemCommand = listAuthorityFomatDaily.stream()
				.filter(e -> e.getSheetNo().intValue() == authorityFormatSheet.getSheetNo().intValue())
				.map(e -> DailyFormItemCommand.toDto(e))
				.collect(Collectors.toList());
		
		return new DailyFormSheetCommand(
				authorityFormatSheet.getSheetNo(),
				authorityFormatSheet.getSheetName(),
				listDailyFormItemCommand);
	}
}
