package nts.uk.screen.at.app.ksu001.aggrerateschedule;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.screen.at.app.ksu001.aggreratepersonaltotal.AggregatePersonalDto;
import nts.uk.screen.at.app.ksu001.aggrerateworkplacetotal.AggregateWorkplaceDto;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AggregateScheduleDto {

	// 個人計集計結果　←集計内容によって情報が異なる
	public AggregatePersonalDto aggreratePersonal;
		
	// ・職場計集計結果　←集計内容によって情報が異なる
	public AggregateWorkplaceDto aggrerateWorkplace;
}
