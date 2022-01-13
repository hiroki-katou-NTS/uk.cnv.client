package nts.uk.screen.at.app.ksu001.aggreratedinformation;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.schedule.app.find.budget.external.ExternalBudgetDto;
import nts.uk.screen.at.app.ksu001.aggrerateschedule.AggregateScheduleDto;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AggregatedInformationDto {
	
	// Map<年月日, Map<外部予算実績項目, 外部予算実績値>>
	public Map<GeneralDate, Map<ExternalBudgetDto, String>> externalBudget = new HashMap<>();
	
	// ・個人計集計結果　←集計内容によって情報が異なる
	// ・職場計集計結果　←集計内容によって情報が異なる
	public AggregateScheduleDto aggrerateSchedule;
	
	
}
