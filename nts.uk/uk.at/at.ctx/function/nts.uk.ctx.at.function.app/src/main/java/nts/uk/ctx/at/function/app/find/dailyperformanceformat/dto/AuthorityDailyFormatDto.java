package nts.uk.ctx.at.function.app.find.dailyperformanceformat.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.function.app.command.dailyperformanceformat.DailyFormSheetCommand;
import nts.uk.ctx.at.function.dom.dailyperformanceformat.AuthorityFomatDaily;
import nts.uk.ctx.at.function.dom.dailyperformanceformat.AuthorityFormatSheet;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthorityDailyFormatDto {
	
	private List<DailyFormSheetCommand> listDailyFormSheetCommand;
	
	public static AuthorityDailyFormatDto toDto(List<AuthorityFormatSheet> listAuthorityFormatSheet, List<AuthorityFomatDaily> listAuthorityFomatDaily) {
		
		List<DailyFormSheetCommand> listDailyFormSheetCommand = listAuthorityFormatSheet.stream()
				.map(e -> DailyFormSheetCommand.toDto(e, listAuthorityFomatDaily))
				.collect(Collectors.toList());
		
		return new AuthorityDailyFormatDto(listDailyFormSheetCommand);
	}
}
